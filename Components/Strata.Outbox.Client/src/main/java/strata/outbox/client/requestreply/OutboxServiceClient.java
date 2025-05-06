//////////////////////////////////////////////////////////////////////////////
// OutboxServiceClient.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.client.requestreply;

import strata.outbox.service.requestreply.*;
import jakarta.ws.rs.client.ClientBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import strata.client.core.service.AbstractRestClient;

import javax.net.ssl.SSLContext;
import java.util.concurrent.CompletionStage;

public
class OutboxServiceClient
    extends AbstractRestClient
    implements IOutboxServiceClient
{
    public
    OutboxServiceClient(String baseUrl)
    {
        super(
            createBuilder(),
            preprocessBaseUrl(baseUrl),
            "outbox-service/");
    }

    @Override
    public CompletionStage<StartWorkerReply>
    startWorker(StartWorkerRequest request)
    {
        return
            doPostAsync(
                "create-outbox",
                StartWorkerReply.class,
                request);
    }

    @Override
    public CompletionStage<StopWorkerReply>
    stopWorker(StopWorkerRequest request)
    {
        return
            doPostAsync(
                "update-outbox",
                StopWorkerReply.class,
                request);
    }

    @Override
    public CompletionStage<QueryWorkerReply>
    queryWorker(QueryWorkerRequest request)
    {
        return
            doPostAsync(
                "destroy-outbox",
                QueryWorkerReply.class,
                request);
    }

    @Override
    public CompletionStage<FindOutboxReply>
    findOutbox(FindOutboxRequest request)
    {
        return
            doPostAsync(
                "find-outbox",
                FindOutboxReply.class,
                request);
    }

    public OutboxServiceClient
    setAuthorizationHeader(String accessToken)
    {
        setHeader("Authorization","Bearer " + accessToken);
        return this;
    }

    private static String
    preprocessBaseUrl(String baseUrl)
    {
        return
            baseUrl.endsWith("/")
                ? baseUrl
                : baseUrl + '/';
    }

    private static ClientBuilder
    createBuilder() throws RuntimeException
    {
        return
            ClientBuilder
                .newBuilder()
                .sslContext(createSslContext());
    }

    private static SSLContext
    createSslContext() throws RuntimeException
    {
        try
        {
            Resource keystore = new ClassPathResource("hello-service.p12");
            return
                new SSLContextBuilder()
                    .loadTrustMaterial(
                        keystore.getURL(),
                        "hello-service".toCharArray())
                    .build();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}

//////////////////////////////////////////////////////////////////////////////
