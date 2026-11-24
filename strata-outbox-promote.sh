#!/usr/bin/env bash
set -euo pipefail

VERSION=""
IMAGE_TAG=""
DRY_RUN=false
FORCE=false
SKIP_MAVEN=false
SKIP_IMAGES=false

NAME="$(basename "$0")"

print_help() {
    cat <<EOF
Usage:
  $NAME --version <x.y.z> [--image-tag <x.y.z-yyyyMMddHHmmss>] [--dry-run] [--force]
                          [--skip-maven] [--skip-images]
  $NAME --help

Promotes published <version>-SNAPSHOT builds of Strata Outbox to release <version>.

  Maven (GitHub Packages, https://maven.pkg.github.com/StrataFrameworkSet/repository):
    Downloads strata-outbox-core, strata-outbox-service, strata-outbox-client,
    strata-outbox-server, strata-outbox-testsuite
    at <version>-SNAPSHOT, rewrites the pom and module to <version>, and deploys them as <version>.
    An existing <version> is deleted (after confirmation) and republished.

  Images (ghcr.io/johnliebenau/strata):
    Tags strata-outbox-servicehost and strata-outbox-testsuite build <version>-<yyyyMMddHHmmss>
    (the newest one, or --image-tag) as <version>. Both images must have that build tag.
    An existing <version> tag on a different image is moved (after confirmation).

Parameters:
  --version <x.y.z>     Release version to promote (required), e.g. 1.0.0
  --image-tag <tag>     Build tag to promote instead of the newest <version>-<timestamp>
  --dry-run             Download and stage everything; print deletes, uploads and tags without
                        changing any registry
  --force               Do not prompt before deleting a Maven release or moving an image tag
  --skip-maven          Skip the Maven artifacts
  --skip-images         Skip the images
  -h, --help            Show this help

Environment:
  REPOSITORY_USER, REPOSITORY_PASSWORD   GitHub Packages and ghcr.io credentials
                                         (token needs read, write and delete:packages)

Requires: curl, node (parses registry responses), sha*sum, md5sum

Examples:
  $NAME --version 1.0.0 --dry-run
  $NAME --version 1.0.0 --force
  $NAME --version 1.0.0 --skip-maven --image-tag 1.0.0-20261124093000
EOF
}

usage() {
    echo "usage: $NAME --version <x.y.z> [--image-tag <tag>] [--dry-run] [--force] [--skip-maven] [--skip-images]" >&2
    echo "       $NAME --help" >&2
    exit 2
}

while [ $# -gt 0 ]; do
    case "$1" in
        --version)      [ $# -ge 2 ] || usage; VERSION="$2"; shift 2 ;;
        --image-tag)    [ $# -ge 2 ] || usage; IMAGE_TAG="$2"; shift 2 ;;
        --dry-run)      DRY_RUN=true; shift ;;
        --force)        FORCE=true; shift ;;
        --skip-maven)   SKIP_MAVEN=true; shift ;;
        --skip-images)  SKIP_IMAGES=true; shift ;;
        -h|--help)      print_help; exit 0 ;;
        *)              echo "unknown option: $1" >&2; usage ;;
    esac
done
[ -n "$VERSION" ] || usage

OWNER="StrataFrameworkSet"
MAVEN_REPO="https://maven.pkg.github.com/$OWNER/repository"
GROUP_ID="strata.outbox"
GROUP_PATH="${GROUP_ID//.//}"
ARTIFACTS=(
    strata-outbox-core
    strata-outbox-service
    strata-outbox-client
    strata-outbox-server
    strata-outbox-testsuite
)
SNAPSHOT="$VERSION-SNAPSHOT"
CHECKSUMS=(sha512 sha256 md5 sha1)

REGISTRY="ghcr.io"
IMAGE_NAMESPACE="johnliebenau/strata"
IMAGES=(
    strata-outbox-servicehost
    strata-outbox-testsuite
)
MANIFEST_TYPES="application/vnd.oci.image.index.v1+json,application/vnd.oci.image.manifest.v1+json,application/vnd.docker.distribution.manifest.list.v2+json,application/vnd.docker.distribution.manifest.v2+json"

step() { printf '\033[36m==> %s\033[0m\n' "$*"; }
info() { printf '    %s\n' "$*"; }
plan() { printf '\033[33m    [dry-run] %s\033[0m\n' "$*"; }
die()  { echo "strata-outbox-promote: $*" >&2; exit 1; }

confirm() {
    $FORCE && return 0
    local answer
    read -r -p "    $1 [y/N] " answer
    [[ "$answer" =~ ^(y|yes)$ ]]
}

http_status() {
    local url="$1"; shift
    curl -s -o /dev/null -w '%{http_code}' "$@" "$url" || true
}

regex_escape() { printf '%s' "$1" | sed 's/[][\.*^$/]/\\&/g'; }

xml_value() { printf '%s' "$1" | sed -n "s#.*<$2>\([^<]*\)</$2>.*#\1#p"; }

new_auth_configs() {
    [ -n "${REPOSITORY_USER:-}" ] && [ -n "${REPOSITORY_PASSWORD:-}" ] \
        || die "REPOSITORY_USER and REPOSITORY_PASSWORD must be set"
    MAVEN_AUTH="$WORK/maven.curlrc"
    API_AUTH="$WORK/api.curlrc"
    ( umask 077
      printf 'user = "%s:%s"\n' "$REPOSITORY_USER" "$REPOSITORY_PASSWORD" > "$MAVEN_AUTH"
      {
          printf 'header = "Authorization: Bearer %s"\n' "$REPOSITORY_PASSWORD"
          printf 'header = "Accept: application/vnd.github+json"\n'
          printf 'header = "X-GitHub-Api-Version: 2022-11-28"\n'
      } > "$API_AUTH" )
}

find_package_version_url() {
    local artifact_id="$1" name="$GROUP_ID.$1" scope page json result
    for scope in "orgs/$OWNER" "users/$OWNER"; do
        page=1
        while true; do
            json=$(curl -sS -f -K "$API_AUTH" \
                "https://api.github.com/$scope/packages/maven/$name/versions?per_page=100&page=$page" 2>/dev/null) || break
            result=$(printf '%s' "$json" | node -e '
                let s = "";
                process.stdin.on("data", d => s += d).on("end", () => {
                    const v = JSON.parse(s);
                    if (!Array.isArray(v) || v.length === 0) { process.stdout.write("END"); return; }
                    const m = v.find(x => x.name === process.argv[1]);
                    process.stdout.write(m ? String(m.id) : "");
                });' "$VERSION")
            [ "$result" = "END" ] && break
            if [ -n "$result" ]; then
                printf '%s %s' "$result" "https://api.github.com/$scope/packages/maven/$name/versions/$result"
                return 0
            fi
            page=$((page + 1))
        done
    done
    return 1
}

write_checksums() {
    local path="$1" algo
    for algo in "${CHECKSUMS[@]}"; do
        "${algo}sum" "$path" | cut -d' ' -f1 | tr -d '\n' > "$path.$algo"
        FILES+=("$path.$algo")
    done
}

verify_download() {
    local path="$1" ext="$2" ok=true
    case "$ext" in
        jar)    [ "$(head -c 2 "$path")" = "PK" ] || ok=false ;;
        pom)    grep -q '<project' "$path" || ok=false ;;
        module) [ "$(tr -d ' \t\r\n' < "$path" | head -c 1)" = "{" ] || ok=false ;;
    esac
    $ok || die "$(basename "$path") is not a valid .$ext file (starts with: $(head -c 60 "$path" | tr -d '\r\n'))"
}

maven_promote() {
    local artifact_id="$1"
    local base="$MAVEN_REPO/$GROUP_PATH/$artifact_id"
    local dir="$WORK/$artifact_id"
    local meta entries entry ext cls val classifier source target path text found
    step "Maven $GROUP_ID:$artifact_id $SNAPSHOT -> $VERSION"
    mkdir -p "$dir"

    meta=$(curl -sS -f -L -K "$MAVEN_AUTH" "$base/$SNAPSHOT/maven-metadata.xml") \
        || die "GET failed: $base/$SNAPSHOT/maven-metadata.xml"
    meta=$(printf '%s' "$meta" | tr -d '\r\n')
    local build ts bn current
    build=$(printf '%s' "$meta" | sed -n 's#.*<snapshot>\(.*\)</snapshot>.*#\1#p')
    ts=$(xml_value "$build" timestamp)
    bn=$(xml_value "$build" buildNumber)
    [ -n "$ts" ] && [ -n "$bn" ] || die "no current snapshot build listed for $artifact_id $SNAPSHOT"
    current="$VERSION-$ts-$bn"
    info "snapshot build: $current"
    entries=$(printf '%s' "$meta" | sed 's#</snapshotVersion>#&\n#g' | grep '<snapshotVersion>' || true)
    [ -n "$entries" ] || die "no snapshot files listed for $artifact_id $SNAPSHOT"

    FILES=()
    found=0
    while IFS= read -r entry; do
        ext=$(xml_value "$entry" extension)
        cls=$(xml_value "$entry" classifier)
        val=$(xml_value "$entry" value)
        [ "$val" = "$current" ] || continue
        case "$ext" in *.sha1|*.md5|*.sha256|*.sha512) continue ;; esac
        classifier=""
        [ -n "$cls" ] && classifier="-$cls"
        source="$artifact_id-$val$classifier.$ext"
        target="$artifact_id-$VERSION$classifier.$ext"
        path="$dir/$target"
        curl -sS -f -L -K "$MAVEN_AUTH" -o "$path" "$base/$SNAPSHOT/$source" || die "download failed: $base/$SNAPSHOT/$source"
        verify_download "$path" "$ext"
        if [ "$ext" = "pom" ] || [ "$ext" = "module" ]; then
            sed -i "s/$(regex_escape "$val")/$VERSION/g; s/$(regex_escape "$SNAPSHOT")/$VERSION/g" "$path"
            if grep -qF -e "$SNAPSHOT" -e "$val" "$path"; then
                die "$target still references $SNAPSHOT after rewrite"
            fi
            info "rewrote $target (no $SNAPSHOT references remain)"
        fi
        FILES+=("$path")
        write_checksums "$path"
        info "staged $target  <=  $source"
        found=$((found + 1))
    done <<< "$entries"
    [ "$found" -gt 0 ] || die "no files listed for snapshot build $current of $artifact_id"

    local release_pom="$base/$VERSION/$artifact_id-$VERSION.pom" existing pom_status id url
    existing=$(find_package_version_url "$artifact_id") || existing=""
    pom_status=$(http_status "$release_pom" -K "$MAVEN_AUTH")
    if [ -n "$existing" ] || [[ "$pom_status" =~ ^(200|301|302|303|307|308)$ ]]; then
        [ -n "$existing" ] \
            || die "$artifact_id $VERSION exists but its GitHub package version id could not be found"
        id="${existing%% *}"
        url="${existing#* }"
        if $DRY_RUN; then
            plan "would DELETE existing $artifact_id $VERSION (package version id $id)"
        elif confirm "$artifact_id $VERSION already exists. Delete it and republish?"; then
            curl -sS -f -K "$API_AUTH" -X DELETE "$url" > /dev/null \
                || die "delete failed for $artifact_id $VERSION (token needs delete:packages)"
            info "deleted existing $artifact_id $VERSION"
        else
            info "skipped $artifact_id (existing $VERSION kept)"
            return 1
        fi
    else
        info "$artifact_id $VERSION does not exist yet"
    fi

    local file
    for file in "${FILES[@]}"; do
        url="$base/$VERSION/$(basename "$file")"
        if $DRY_RUN; then
            plan "would PUT $url"
        else
            curl -sS -f -K "$MAVEN_AUTH" -T "$file" "$url" > /dev/null || die "upload failed: $url"
        fi
    done
    $DRY_RUN || info "published $artifact_id $VERSION (${#FILES[@]} files)"
    return 0
}

registry_auth() {
    local repo="$1" json token rc
    rc="$WORK/registry-${repo//\//-}.curlrc"
    json=$(curl -sS -f -K "$MAVEN_AUTH" \
        "https://$REGISTRY/token?service=$REGISTRY&scope=repository:$repo:pull,push") \
        || die "cannot get a $REGISTRY token for $repo"
    token=$(printf '%s' "$json" | node -e '
        let s = "";
        process.stdin.on("data", d => s += d).on("end", () => {
            const v = JSON.parse(s);
            process.stdout.write(v.token || v.access_token || "");
        });')
    [ -n "$token" ] || die "empty $REGISTRY token for $repo"
    ( umask 077
      printf 'header = "Authorization: Bearer %s"\n' "$token" > "$rc" )
    printf '%s' "$rc"
}

latest_build_tag() {
    local repo="$1" rc="$2" json
    json=$(curl -sS -f -K "$rc" "https://$REGISTRY/v2/$repo/tags/list?n=10000") \
        || die "cannot list tags of $REGISTRY/$repo"
    printf '%s' "$json" | node -e '
        let s = "";
        process.stdin.on("data", d => s += d).on("end", () => {
            const v = JSON.parse(s);
            const prefix = process.argv[1] + "-";
            const tags = (v.tags || []).filter(t =>
                t.startsWith(prefix) && /^[0-9]{14}$/.test(t.slice(prefix.length)));
            tags.sort();
            process.stdout.write(tags.length ? tags[tags.length - 1] : "");
        });' "$VERSION"
}

header_value() { grep -i "^$2:" "$1" | tail -n 1 | cut -d' ' -f2- | tr -d '\r'; }

image_promote() {
    local image="$1" repo="$IMAGE_NAMESPACE/$1" rc dir status source_digest target_digest content_type
    step "Image $REGISTRY/$repo $IMAGE_TAG -> $VERSION"
    rc=$(registry_auth "$repo")
    dir="$WORK/image-$image"
    mkdir -p "$dir"

    curl -sS -f -K "$rc" -H "Accept: $MANIFEST_TYPES" -D "$dir/source.headers" -o "$dir/manifest.json" \
        "https://$REGISTRY/v2/$repo/manifests/$IMAGE_TAG" \
        || die "$REGISTRY/$repo:$IMAGE_TAG not found"
    source_digest=$(header_value "$dir/source.headers" docker-content-digest)
    content_type=$(header_value "$dir/source.headers" content-type)
    [ -n "$source_digest" ] && [ -n "$content_type" ] \
        || die "incomplete manifest response for $repo:$IMAGE_TAG"
    info "build $IMAGE_TAG is $source_digest"

    status=$(curl -s -o /dev/null -D "$dir/target.headers" -w '%{http_code}' -K "$rc" -I \
        -H "Accept: $MANIFEST_TYPES" "https://$REGISTRY/v2/$repo/manifests/$VERSION" || true)
    if [ "$status" = "200" ]; then
        target_digest=$(header_value "$dir/target.headers" docker-content-digest)
        if [ "$target_digest" = "$source_digest" ]; then
            info "$repo:$VERSION already points to $IMAGE_TAG"
            return 0
        fi
        if $DRY_RUN; then
            plan "would MOVE $repo:$VERSION from $target_digest to $source_digest"
            return 0
        elif ! confirm "$repo:$VERSION already exists ($target_digest). Move it to $IMAGE_TAG?"; then
            info "skipped $image (existing $VERSION kept)"
            return 1
        fi
    else
        info "$repo:$VERSION does not exist yet"
    fi

    if $DRY_RUN; then
        plan "would PUT $REGISTRY/$repo:$VERSION -> $source_digest"
    else
        curl -sS -f -K "$rc" -X PUT -H "Content-Type: $content_type" \
            --data-binary "@$dir/manifest.json" \
            "https://$REGISTRY/v2/$repo/manifests/$VERSION" > /dev/null \
            || die "tagging $repo:$VERSION failed (token needs write:packages)"
        info "tagged $REGISTRY/$repo:$VERSION"
    fi
    return 0
}

resolve_image_tag() {
    local first="$IMAGE_NAMESPACE/${IMAGES[0]}" rc
    if [ -n "$IMAGE_TAG" ]; then
        printf '%s' "$IMAGE_TAG" | grep -Eq "^$(regex_escape "$VERSION")-[0-9]{14}\$" \
            || die "--image-tag must look like $VERSION-<yyyyMMddHHmmss>"
        return 0
    fi
    rc=$(registry_auth "$first")
    IMAGE_TAG=$(latest_build_tag "$first" "$rc")
    [ -n "$IMAGE_TAG" ] || die "no $VERSION-<timestamp> build of $REGISTRY/$first found"
    step "Newest image build: $IMAGE_TAG"
}

shopt -s nocasematch
[[ "$VERSION" == *snapshot* ]] && die "pass the release version (e.g. 1.0.0), not a snapshot"
shopt -u nocasematch

WORK=$(mktemp -d "${TMPDIR:-/tmp}/strata-outbox-promote-XXXXXX")
trap 'rm -rf "$WORK"' EXIT
SKIPPED=()

$DRY_RUN && step "DRY RUN: no registry will be changed"
new_auth_configs
if ! $SKIP_MAVEN; then
    for artifact_id in "${ARTIFACTS[@]}"; do
        maven_promote "$artifact_id" || SKIPPED+=("maven:$artifact_id")
    done
fi
if ! $SKIP_IMAGES; then
    resolve_image_tag
    for image in "${IMAGES[@]}"; do
        image_promote "$image" || SKIPPED+=("image:$image")
    done
fi
if [ ${#SKIPPED[@]} -gt 0 ]; then
    step "Done; skipped: ${SKIPPED[*]}"
else
    step "Done"
fi
