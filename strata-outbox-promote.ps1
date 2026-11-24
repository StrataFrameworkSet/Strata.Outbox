[CmdletBinding(DefaultParameterSetName = 'Run')]
param(
    [Parameter(Mandatory = $true, ParameterSetName = 'Run')][string]$Version,
    [Parameter(ParameterSetName = 'Run')][string]$ImageTag,
    [switch]$DryRun,
    [switch]$Force,
    [switch]$SkipMaven,
    [switch]$SkipImages,
    [Parameter(Mandatory = $true, ParameterSetName = 'Help')][switch]$Help
)

$ErrorActionPreference = 'Stop'

$Owner      = 'StrataFrameworkSet'
$MavenRepo  = "https://maven.pkg.github.com/$Owner/repository"
$GroupId    = 'strata.outbox'
$GroupPath  = $GroupId.Replace('.', '/')
$Artifacts  = @(
    'strata-outbox-core',
    'strata-outbox-service',
    'strata-outbox-client',
    'strata-outbox-server',
    'strata-outbox-testsuite'
)
$Snapshot    = "$Version-SNAPSHOT"
$Checksums  = @{ sha1 = 'SHA1'; md5 = 'MD5'; sha256 = 'SHA256'; sha512 = 'SHA512' }
$Utf8NoBom  = New-Object System.Text.UTF8Encoding $false

$Registry       = 'ghcr.io'
$ImageNamespace = 'johnliebenau/strata'
$Images         = @(
    'strata-outbox-servicehost',
    'strata-outbox-testsuite'
)
$ManifestTypes  = 'application/vnd.oci.image.index.v1+json,application/vnd.oci.image.manifest.v1+json,application/vnd.docker.distribution.manifest.list.v2+json,application/vnd.docker.distribution.manifest.v2+json'

function Write-Step([string]$Message) { Write-Host "==> $Message" -ForegroundColor Cyan }
function Write-Info([string]$Message) { Write-Host "    $Message" }
function Write-Plan([string]$Message) { Write-Host "    [dry-run] $Message" -ForegroundColor Yellow }
function Stop-Promote([string]$Message) { throw "strata-outbox-promote: $Message" }

function Confirm-Action([string]$Message) {
    if ($Force) { return $true }
    $answer = Read-Host "    $Message [y/N]"
    return ($answer -match '^(y|yes)$')
}

function Get-HttpStatus([string]$Url, [string]$AuthConfig) {
    $arguments = @('-s', '-o', 'NUL', '-w', '%{http_code}')
    if ($AuthConfig) { $arguments += @('-K', $AuthConfig) }
    $code = & curl.exe @arguments $Url
    return [int]$code
}

function Get-Url([string]$Url, [string]$AuthConfig) {
    $output = & curl.exe -sS -f -L -K $AuthConfig $Url
    if ($LASTEXITCODE -ne 0) { Stop-Promote "GET failed: $Url" }
    return ($output -join "`n")
}

function Save-Url([string]$Url, [string]$Destination, [string]$AuthConfig) {
    & curl.exe -sS -f -L -K $AuthConfig -o $Destination $Url
    if ($LASTEXITCODE -ne 0) { Stop-Promote "download failed: $Url" }
}

function Test-Download([string]$Path, [string]$Extension) {
    $bytes = [System.IO.File]::ReadAllBytes($Path)
    $text  = [System.Text.Encoding]::UTF8.GetString($bytes)
    $ok = switch ($Extension) {
        'jar'    { $bytes.Length -ge 2 -and $bytes[0] -eq 0x50 -and $bytes[1] -eq 0x4B }
        'pom'    { $text.Contains('<project') }
        'module' { $text.TrimStart().StartsWith('{') }
        default  { $true }
    }
    if (-not $ok) {
        $start = $text.Substring(0, [Math]::Min(60, $text.Length)) -replace '[\r\n]', ''
        Stop-Promote "$(Split-Path -Leaf $Path) is not a valid .$Extension file (starts with: $start)"
    }
}

function Send-File([string]$Path, [string]$Url, [string]$AuthConfig) {
    & curl.exe -sS -f -K $AuthConfig -T $Path $Url | Out-Null
    if ($LASTEXITCODE -ne 0) { Stop-Promote "upload failed: $Url" }
}

function New-AuthConfigs([string]$WorkDir) {
    if (-not $env:REPOSITORY_USER -or -not $env:REPOSITORY_PASSWORD) {
        Stop-Promote 'REPOSITORY_USER and REPOSITORY_PASSWORD must be set'
    }
    $maven = Join-Path $WorkDir 'maven.curlrc'
    $api   = Join-Path $WorkDir 'api.curlrc'
    [System.IO.File]::WriteAllText($maven, "user = `"$($env:REPOSITORY_USER):$($env:REPOSITORY_PASSWORD)`"`n", $Utf8NoBom)
    [System.IO.File]::WriteAllText($api, (@(
        "header = `"Authorization: Bearer $($env:REPOSITORY_PASSWORD)`"",
        'header = "Accept: application/vnd.github+json"',
        'header = "X-GitHub-Api-Version: 2022-11-28"'
    ) -join "`n") + "`n", $Utf8NoBom)
    return @{ Maven = $maven; Api = $api }
}

function Get-PackageVersion([string]$ArtifactId, [string]$ApiAuth) {
    $name = "$GroupId.$ArtifactId"
    foreach ($scope in @("orgs/$Owner", "users/$Owner")) {
        $page = 1
        while ($true) {
            $url  = "https://api.github.com/$scope/packages/maven/$name/versions?per_page=100&page=$page"
            $json = & curl.exe -sS -f -K $ApiAuth $url 2>$null
            if ($LASTEXITCODE -ne 0) { break }
            $parsed   = ($json -join "`n") | ConvertFrom-Json
            $versions = @($parsed | ForEach-Object { $_ })
            if ($versions.Count -eq 0) { break }
            $match = @($versions | Where-Object { $_.name -eq $Version })
            if ($match.Count -gt 1) { Stop-Promote "multiple GitHub package versions named $Version for $name" }
            $match = $match | Select-Object -First 1
            if ($match) { return @{ Url = "https://api.github.com/$scope/packages/maven/$name/versions/$($match.id)"; Id = $match.id } }
            $page++
        }
    }
    return $null
}

function Write-Checksums([string]$Path) {
    $files = @()
    foreach ($ext in $Checksums.Keys) {
        $hash = (Get-FileHash -Path $Path -Algorithm $Checksums[$ext]).Hash.ToLowerInvariant()
        $out  = "$Path.$ext"
        [System.IO.File]::WriteAllText($out, $hash, $Utf8NoBom)
        $files += $out
    }
    return $files
}

function Invoke-MavenPromote([string]$ArtifactId, [hashtable]$Auth, [string]$WorkDir) {
    Write-Step "Maven $GroupId`:$ArtifactId $Snapshot -> $Version"
    $base = "$MavenRepo/$GroupPath/$ArtifactId"
    $dir  = Join-Path $WorkDir $ArtifactId
    New-Item -ItemType Directory -Path $dir | Out-Null

    [xml]$meta = Get-Url "$base/$Snapshot/maven-metadata.xml" $Auth.Maven
    $build = $meta.metadata.versioning.snapshot
    if (-not $build -or -not $build.timestamp -or -not $build.buildNumber) {
        Stop-Promote "no current snapshot build listed for $ArtifactId $Snapshot"
    }
    $current = "$Version-$($build.timestamp)-$($build.buildNumber)"
    Write-Info "snapshot build: $current"
    $entries = @($meta.metadata.versioning.snapshotVersions.snapshotVersion |
        Where-Object { $_.value -eq $current -and $_.extension -notmatch '\.(sha1|md5|sha256|sha512)$' })
    if ($entries.Count -eq 0) { Stop-Promote "no files listed for snapshot build $current of $ArtifactId" }

    $files = @()
    foreach ($entry in $entries) {
        $classifier = if ($entry.classifier) { "-$($entry.classifier)" } else { '' }
        $source = "$ArtifactId-$($entry.value)$classifier.$($entry.extension)"
        $target = "$ArtifactId-$Version$classifier.$($entry.extension)"
        $path   = Join-Path $dir $target
        Save-Url "$base/$Snapshot/$source" $path $Auth.Maven
        Test-Download $path $entry.extension
        if ($entry.extension -in @('pom', 'module')) {
            $text = [System.IO.File]::ReadAllText($path)
            $text = $text.Replace($entry.value, $Version).Replace($Snapshot, $Version)
            [System.IO.File]::WriteAllText($path, $text, $Utf8NoBom)
            if ($text.Contains($Snapshot) -or $text.Contains($entry.value)) {
                Stop-Promote "$target still references $Snapshot after rewrite"
            }
            Write-Info "rewrote $target (no $Snapshot references remain)"
        }
        $files += $path
        $files += Write-Checksums $path
        Write-Info "staged $target  <=  $source"
    }

    $releasePom = "$base/$Version/$ArtifactId-$Version.pom"
    $existing   = Get-PackageVersion $ArtifactId $Auth.Api
    $pomStatus  = Get-HttpStatus $releasePom $Auth.Maven
    if ($existing -or $pomStatus -in @(200, 301, 302, 303, 307, 308)) {
        if (-not $existing) { Stop-Promote "$ArtifactId $Version exists but its GitHub package version id could not be found" }
        if ($DryRun) {
            Write-Plan "would DELETE existing $ArtifactId $Version (package version id $($existing.Id))"
        } elseif (Confirm-Action "$ArtifactId $Version already exists. Delete it and republish?") {
            & curl.exe -sS -f -K $Auth.Api -X DELETE $existing.Url | Out-Null
            if ($LASTEXITCODE -ne 0) { Stop-Promote "delete failed for $ArtifactId $Version (token needs delete:packages)" }
            Write-Info "deleted existing $ArtifactId $Version"
        } else {
            Write-Info "skipped $ArtifactId (existing $Version kept)"
            return $false
        }
    } else {
        Write-Info "$ArtifactId $Version does not exist yet"
    }

    foreach ($file in $files) {
        $url = "$base/$Version/$(Split-Path -Leaf $file)"
        if ($DryRun) { Write-Plan "would PUT $url" } else { Send-File $file $url $Auth.Maven }
    }
    if (-not $DryRun) { Write-Info "published $ArtifactId $Version ($($files.Count) files)" }
    return $true
}

function Get-HeaderValue([string]$HeaderFile, [string]$Name) {
    $line = Get-Content $HeaderFile | Where-Object { $_ -match "^$([regex]::Escape($Name)):" } | Select-Object -Last 1
    if (-not $line) { return $null }
    return ($line -split ':', 2)[1].Trim()
}

function New-RegistryAuth([string]$Repository, [hashtable]$Auth, [string]$WorkDir) {
    $json = & curl.exe -sS -f -K $Auth.Maven "https://$Registry/token?service=$Registry&scope=repository:$Repository`:pull,push"
    if ($LASTEXITCODE -ne 0) { Stop-Promote "cannot get a $Registry token for $Repository" }
    $parsed = ($json -join "`n") | ConvertFrom-Json
    $token  = if ($parsed.token) { $parsed.token } else { $parsed.access_token }
    if (-not $token) { Stop-Promote "empty $Registry token for $Repository" }
    $rc = Join-Path $WorkDir ("registry-" + $Repository.Replace('/', '-') + ".curlrc")
    [System.IO.File]::WriteAllText($rc, "header = `"Authorization: Bearer $token`"`n", $Utf8NoBom)
    return $rc
}

function Get-LatestBuildTag([string]$Repository, [string]$RegistryAuth) {
    $json = & curl.exe -sS -f -K $RegistryAuth "https://$Registry/v2/$Repository/tags/list?n=10000"
    if ($LASTEXITCODE -ne 0) { Stop-Promote "cannot list tags of $Registry/$Repository" }
    $parsed  = ($json -join "`n") | ConvertFrom-Json
    $pattern = '^' + [regex]::Escape($Version) + '-[0-9]{14}$'
    $tags    = @($parsed.tags | Where-Object { $_ -match $pattern } | Sort-Object)
    if ($tags.Count -eq 0) { return $null }
    return $tags[-1]
}

function Resolve-ImageTag([hashtable]$Auth, [string]$WorkDir) {
    if ($ImageTag) {
        if ($ImageTag -notmatch ('^' + [regex]::Escape($Version) + '-[0-9]{14}$')) {
            Stop-Promote "-ImageTag must look like $Version-<yyyyMMddHHmmss>"
        }
        return $ImageTag
    }
    $first = "$ImageNamespace/$($Images[0])"
    $rc    = New-RegistryAuth $first $Auth $WorkDir
    $tag   = Get-LatestBuildTag $first $rc
    if (-not $tag) { Stop-Promote "no $Version-<timestamp> build of $Registry/$first found" }
    Write-Step "Newest image build: $tag"
    return $tag
}

function Invoke-ImagePromote([string]$Image, [string]$BuildTag, [hashtable]$Auth, [string]$WorkDir) {
    $repository = "$ImageNamespace/$Image"
    Write-Step "Image $Registry/$repository $BuildTag -> $Version"
    $rc  = New-RegistryAuth $repository $Auth $WorkDir
    $dir = Join-Path $WorkDir "image-$Image"
    New-Item -ItemType Directory -Path $dir | Out-Null
    $manifest      = Join-Path $dir 'manifest.json'
    $sourceHeaders = Join-Path $dir 'source.headers'
    $targetHeaders = Join-Path $dir 'target.headers'

    & curl.exe -sS -f -K $rc -H "Accept: $ManifestTypes" -D $sourceHeaders -o $manifest "https://$Registry/v2/$repository/manifests/$BuildTag"
    if ($LASTEXITCODE -ne 0) { Stop-Promote "$Registry/$repository`:$BuildTag not found" }
    $sourceDigest = Get-HeaderValue $sourceHeaders 'docker-content-digest'
    $contentType  = Get-HeaderValue $sourceHeaders 'content-type'
    if (-not $sourceDigest -or -not $contentType) { Stop-Promote "incomplete manifest response for $repository`:$BuildTag" }
    Write-Info "build $BuildTag is $sourceDigest"

    $status = & curl.exe -s -o NUL -D $targetHeaders -w '%{http_code}' -K $rc -I -H "Accept: $ManifestTypes" "https://$Registry/v2/$repository/manifests/$Version"
    if ([int]$status -eq 200) {
        $targetDigest = Get-HeaderValue $targetHeaders 'docker-content-digest'
        if ($targetDigest -eq $sourceDigest) {
            Write-Info "$repository`:$Version already points to $BuildTag"
            return $true
        }
        if ($DryRun) {
            Write-Plan "would MOVE $repository`:$Version from $targetDigest to $sourceDigest"
            return $true
        } elseif (-not (Confirm-Action "$repository`:$Version already exists ($targetDigest). Move it to $BuildTag?")) {
            Write-Info "skipped $Image (existing $Version kept)"
            return $false
        }
    } else {
        Write-Info "$repository`:$Version does not exist yet"
    }

    if ($DryRun) {
        Write-Plan "would PUT $Registry/$repository`:$Version -> $sourceDigest"
    } else {
        & curl.exe -sS -f -K $rc -X PUT -H "Content-Type: $contentType" --data-binary "@$manifest" "https://$Registry/v2/$repository/manifests/$Version" | Out-Null
        if ($LASTEXITCODE -ne 0) { Stop-Promote "tagging $repository`:$Version failed (token needs write:packages)" }
        Write-Info "tagged $Registry/$repository`:$Version"
    }
    return $true
}

function Show-Help {
    $name = Split-Path -Leaf $PSCommandPath
    Write-Host @"
Usage:
  $name -Version <x.y.z> [-ImageTag <x.y.z-yyyyMMddHHmmss>] [-DryRun] [-Force] [-SkipMaven] [-SkipImages]
  $name -Help

Promotes published <version>-SNAPSHOT builds of Strata Outbox to release <version>.

  Maven (GitHub Packages, $MavenRepo):
    Downloads $($Artifacts -join ', ')
    at <version>-SNAPSHOT, rewrites the pom and module to <version>, and deploys them as <version>.
    An existing <version> is deleted (after confirmation) and republished.

  Images ($Registry/$ImageNamespace):
    Tags $($Images -join ' and ') build <version>-<yyyyMMddHHmmss>
    (the newest one, or -ImageTag) as <version>. Both images must have that build tag.
    An existing <version> tag on a different image is moved (after confirmation).

Parameters:
  -Version <x.y.z>     Release version to promote (required), e.g. 1.0.0
  -ImageTag <tag>      Build tag to promote instead of the newest <version>-<timestamp>
  -DryRun              Download and stage everything; print deletes, uploads and tags without
                       changing any registry
  -Force               Do not prompt before deleting a Maven release or moving an image tag
  -SkipMaven           Skip the Maven artifacts
  -SkipImages          Skip the images
  -Help                Show this help

Environment:
  REPOSITORY_USER, REPOSITORY_PASSWORD   GitHub Packages and ghcr.io credentials
                                         (token needs read, write and delete:packages)

Examples:
  $name -Version 1.0.0 -DryRun
  $name -Version 1.0.0 -Force
  $name -Version 1.0.0 -SkipMaven -ImageTag 1.0.0-20261124093000
"@
}

if ($Help) { Show-Help; return }

if ($Version -match '(?i)snapshot') { Stop-Promote "pass the release version (e.g. 1.0.0), not a snapshot" }

$work = Join-Path ([System.IO.Path]::GetTempPath()) ("strata-outbox-promote-" + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $work | Out-Null
$skipped = @()
try {
    if ($DryRun) { Write-Step 'DRY RUN: no registry will be changed' }
    $auth = New-AuthConfigs $work
    if (-not $SkipMaven) {
        foreach ($artifactId in $Artifacts) {
            if (-not (Invoke-MavenPromote $artifactId $auth $work)) { $skipped += "maven:$artifactId" }
        }
    }
    if (-not $SkipImages) {
        $buildTag = Resolve-ImageTag $auth $work
        foreach ($image in $Images) {
            if (-not (Invoke-ImagePromote $image $buildTag $auth $work)) { $skipped += "image:$image" }
        }
    }
    if ($skipped.Count -gt 0) { Write-Step "Done; skipped: $($skipped -join ', ')" } else { Write-Step 'Done' }
} finally {
    Remove-Item -Recurse -Force $work -ErrorAction SilentlyContinue
}
