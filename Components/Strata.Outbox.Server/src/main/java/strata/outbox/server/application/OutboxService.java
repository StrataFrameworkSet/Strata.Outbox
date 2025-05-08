//////////////////////////////////////////////////////////////////////////////
// OutboxService.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.*;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.foundation.core.transfer.ExceptionData;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public
class OutboxService
    implements IOutboxService
{
    private final IOutboxWorker  worker;
    private final Logger                 logger;

    @Inject
    public
    OutboxService(IOutboxWorker worker)
    {
        this.worker = worker;
        logger      = LogManager.getLogger(OutboxService.class);
    }

    @Override
    public CompletionStage<StartWorkerReply>
    startWorker(StartWorkerRequest request)
    {
        logger.debug("startWorker({})",request.getRequestId());
        return
            CompletableFuture
                .supplyAsync(() -> mapToContext(request))
                .thenApply(context -> startWorker(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    @Override
    public CompletionStage<StopWorkerReply>
    stopWorker(StopWorkerRequest request)
    {
        logger.debug("stopWorker({})",request.getRequestId());
        return
            CompletableFuture
                .supplyAsync(() -> mapToContext(request))
                .thenApply(context -> stopWorker(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    @Override
    public CompletionStage<QueryWorkerReply>
    queryWorker(QueryWorkerRequest request)
    {
        logger.debug("queryWorker({})",request.getRequestId());
        return
            CompletableFuture
                .supplyAsync(() -> mapToContext(request))
                .thenApply(context -> queryWorker(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    protected StartWorkerContext
    mapToContext(StartWorkerRequest request)
    {
        logger.debug("<startWorker> mapToContext({})",request.getRequestId());
        return new StartWorkerContext(request);
    }

    protected StopWorkerContext
    mapToContext(StopWorkerRequest request)
    {
        logger.debug("<stopWorker> mapToContext({})",request.getRequestId());
        return new StopWorkerContext(request);
    }

    protected QueryWorkerContext
    mapToContext(QueryWorkerRequest request)
    {
        logger.debug("<queryWorker> mapToContext({})",request.getRequestId());
        return new QueryWorkerContext(request);
    }

    protected StartWorkerContext
    startWorker(StartWorkerContext context)
    {
        logger.debug("starting worker");
        worker.start();

        for (int i=1;i<=3;i++)
        {
            try
            {
                context.setWorking(worker.isWorking());

                if (context.isWorking())
                    return context;

                Thread.sleep(10*i);
            }
            catch (InterruptedException e) {}
        }

        return context;
    }

    protected StopWorkerContext
    stopWorker(StopWorkerContext context)
    {
        logger.debug("stopping worker");
        worker.stop();
        return context.setWorking(worker.isWorking());
    }

    protected QueryWorkerContext
    queryWorker(QueryWorkerContext context)
    {
        logger.debug("querying worker");
        return context.setWorking(worker.isWorking());
    }

    private StartWorkerReply
    buildReply(StartWorkerContext context)
    {
        logger.info("building successful reply");

        return
            context.isWorking()
                ? new StartWorkerReply(context.getRequest())
                    .setSuccess(true)
                    .setSuccessMessage("Outbox worker has started")
                : new StartWorkerReply(context.getRequest())
                    .setSuccess(false)
                    .setFailureMessage("Failed to start outbox worker");
    }

    private StartWorkerReply
    buildReply(Throwable e,StartWorkerRequest request)
    {
        logger.info("building exception reply: {}",e.getMessage());
        return
            new StartWorkerReply(request)
                .setSuccess(false)
                .setFailureMessage("Failed to start outbox worker")
                .setException(ExceptionData.of(e));
    }

    private StopWorkerReply
    buildReply(StopWorkerContext context)
    {
        logger.info("building successful reply");

        return
            !context.isWorking()
                ? new StopWorkerReply(context.getRequest())
                    .setSuccess(true)
                    .setSuccessMessage("Outbox worker has stopped")
                : new StopWorkerReply(context.getRequest())
                    .setSuccess(false)
                    .setFailureMessage("Failed to stop outbox worker");
    }

    private StopWorkerReply
    buildReply(Throwable e,StopWorkerRequest request)
    {
        logger.info("building exception reply: {}",e.getMessage());
        return
            new StopWorkerReply(request)
                .setSuccess(false)
                .setFailureMessage("Failed to stop outbox worker")
                .setException(ExceptionData.of(e));
    }

    private QueryWorkerReply
    buildReply(QueryWorkerContext context)
    {
        logger.info("building successful reply");

        return
            new QueryWorkerReply(context.getRequest())
                .setSuccess(true)
                .setSuccessMessage(
                    context.isWorking()
                        ? "Outbox worker is currently working"
                        : "Outbox worker is currently idle");
    }

    private QueryWorkerReply
    buildReply(Throwable e,QueryWorkerRequest request)
    {
        logger.info("building exception reply: " + e.getMessage());
        return
            new QueryWorkerReply(request)
                .setSuccess(false)
                .setFailureMessage("Unable to query outbox worker")
                .setException(ExceptionData.of(e));
    }
}

//////////////////////////////////////////////////////////////////////////////
