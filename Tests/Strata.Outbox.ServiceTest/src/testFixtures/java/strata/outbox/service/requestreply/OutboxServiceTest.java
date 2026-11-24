//////////////////////////////////////////////////////////////////////////////
// OutboxServiceClientTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationStage")
public abstract
class OutboxServiceTest
{
    private IOutboxService subject;

    @Test
    public void
    testStartWorker()
    {
        StartWorkerReply reply =
            getSubject()
                .startWorker(new StartWorkerRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());

    }

    @Test
    public void
    testStopWorker()
    {
        StartWorkerReply startReply =
            getSubject()
                .startWorker(new StartWorkerRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(startReply.isSuccess(),startReply.getFailureMessage());

        StopWorkerReply stopReply =
            getSubject()
                .stopWorker(new StopWorkerRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(stopReply.isSuccess(),stopReply.getFailureMessage());
    }

    @Test
    public void
    testQueryWorker()
    {
        StartWorkerReply createReply =
            getSubject()
                .startWorker(new StartWorkerRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(createReply.isSuccess(),createReply.getFailureMessage());

        QueryWorkerReply queryReply =
            getSubject()
                .queryWorker(new QueryWorkerRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(queryReply.isSuccess(),queryReply.getFailureMessage());
    }

    protected abstract IOutboxService
    getSubject();
}

//////////////////////////////////////////////////////////////////////////////
