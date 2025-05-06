//////////////////////////////////////////////////////////////////////////////
// OutboxService.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.event.IOutboxEventSender;
import strata.outbox.service.event.OutboxEvent;
import strata.outbox.service.requestreply.*;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import strata.foundation.core.concurrent.CurrentThreadExecutor;
import strata.foundation.core.event.EventIdentifiersData;
import strata.foundation.core.event.StandardEventType;
import strata.foundation.core.transfer.ExceptionData;
import strata.server.core.domainevent.IDomainEventObserver;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public
class OutboxService
    implements ITransactionalOutboxService, IDomainEventObserver<IOutboxEvent>
{
    private final IOutboxRepository  repository;
    private final IOutboxEventSender sender;
    private final ModelMapper            mapper;
    private final Logger                 logger;

    @Inject
    public
    OutboxService(IOutboxRepository r,IOutboxEventSender s,ModelMapper m)
    {
        repository = r;
        sender     = s;
        mapper     = m;
        logger     = LogManager.getLogger(OutboxService.class);
    }

    @Override
    public CompletionStage<StartWorkerReply>
    startWorker(StartWorkerRequest request)
    {
        logger.debug("createOutbox(" + request.getRequestId() + ")");
        return
            CompletableFuture
                .supplyAsync(() -> validate(request),new CurrentThreadExecutor())
                .thenApply(createRequest -> mapToContext(createRequest))
                .thenApply(context -> createOutbox(context))
                .thenApply(context -> saveOutbox(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    @Override
    public CompletionStage<StopWorkerReply>
    stopWorker(StopWorkerRequest request)
    {
        logger.debug("updateOutbox(" + request.getRequestId() + ")");
        return
            CompletableFuture
                .supplyAsync(() -> validate(request),new CurrentThreadExecutor())
                .thenApply(updateRequest -> mapToContext(updateRequest))
                .thenApply(context -> findOutbox(context))
                .thenApply(context -> updateOutbox(context))
                .thenApply(context -> saveOutbox(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    @Override
    public CompletionStage<QueryWorkerReply>
    queryWorker(QueryWorkerRequest request)
    {
        logger.debug("destroyOutbox(" + request.getRequestId() + ")");
        return
            CompletableFuture
                .supplyAsync(() -> validate(request),new CurrentThreadExecutor())
                .thenApply(destroyRequest -> mapToContext(destroyRequest))
                .thenApply(context -> findOutbox(context))
                .thenApply(context -> deleteOutbox(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    @Override
    public CompletionStage<FindOutboxReply>
    findOutbox(FindOutboxRequest request)
    {
        logger.debug("findOutbox(" + request.getRequestId() + ")");
        return
            CompletableFuture
                .supplyAsync(() -> validate(request),new CurrentThreadExecutor())
                .thenApply(findRequest -> mapToContext(findRequest))
                .thenApply(context -> findOutbox(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    @Override
    public void
    onEvent(IOutboxEvent event)
    {
        switch (event.getName())
        {
            case "OutboxCreated":
                logger.debug("sending OutboxCreated event");
                sender.send(toServiceEvent(event,StandardEventType.CREATED));
                break;

            case "OutboxUpdated":
                logger.debug("sending OutboxUpdated event");
                sender.send(toServiceEvent(event,StandardEventType.UPDATED));
                break;

            case "OutboxDestroyed":
                logger.debug("sending OutboxDestroyed event");
                sender.send(toServiceEvent(event,StandardEventType.DELETED));
                break;

            default:
                logger.error("Encountered unknown event type: " + event.getName());
                break;
        }
    }

    @Override
    public void
    onException(Exception e)
    {
        logger.error("Observing OutboxEvents and caught exception: " + e.getMessage());
    }

    protected StartWorkerRequest
    validate(StartWorkerRequest request)
    {
        logger.debug("<create> validate(" + request.getRequestId() + ")");

        Objects.requireNonNull(
            request.getOutbox(),
            "validation failed: foo was not provided");
        return request;
    }

    protected StopWorkerRequest
    validate(StopWorkerRequest request)
    {
        logger.debug("<update> validate(" + request.getRequestId() + ")");

        Objects.requireNonNull(
            request.getOutbox(),
            "validation failed: id was not provided");
        return request;
    }

    protected QueryWorkerRequest
    validate(QueryWorkerRequest request)
    {
        logger.debug("<destroy> validate(" + request.getRequestId() + ")");

        Objects.requireNonNull(
            request.getOutboxId(),
            "validation failed: id was not provided");
        return request;
    }

    protected FindOutboxRequest
    validate(FindOutboxRequest request)
    {
        logger.debug("<find> validate(" + request.getRequestId() + ")");

        Objects.requireNonNull(
            request.getOutboxId(),
            "validation failed: id was not provided");
        return request;
    }

    protected CreateOutboxContext
    mapToContext(StartWorkerRequest request)
    {
        logger.debug("<create> mapToContext(" + request.getRequestId() + ")");
        return new CreateOutboxContext(request);
    }

    protected UpdateOutboxContext
    mapToContext(StopWorkerRequest request)
    {
        logger.debug("<update> mapToContext(" + request.getRequestId() + ")");
        return new UpdateOutboxContext(request);
    }

    protected DestroyOutboxContext
    mapToContext(QueryWorkerRequest request)
    {
        logger.debug("<destroy> mapToContext(" + request.getRequestId() + ")");
        return new DestroyOutboxContext(request);
    }

    protected FindOutboxContext
    mapToContext(FindOutboxRequest request)
    {
        logger.debug("<find> mapToContext(" + request.getRequestId() + ")");
        return new FindOutboxContext(request);
    }

    protected CreateOutboxContext
    createOutbox(CreateOutboxContext context)
    {
        logger.debug("create Outbox");
        return
            context
                .setCreatedOutbox(
                    mapper
                        .map(context.getOutboxToCreate(),Outbox.class)
                        .attach(this));
    }

    protected UpdateOutboxContext
    findOutbox(UpdateOutboxContext context)
    {
        logger.debug("<update> find Outbox");
        return
            context
                .setUpdatedOutbox(
                    repository
                        .findById(
                            context
                                .getOutboxToUpdate()
                                .getOutboxId())
                        .map(entity -> entity.attach(this)));
    }

    protected DestroyOutboxContext
    findOutbox(DestroyOutboxContext context)
    {
        logger.debug("<destroy> find Outbox");
        return
            context
                .setDestroyedOutbox(
                    repository
                        .findById(context.getOutboxId())
                        .map(entity -> entity.attach(this)));
    }

    protected FindOutboxContext
    findOutbox(FindOutboxContext context)
    {
        logger.debug("<find> find Outbox");
        return
            context
                .setFoundOutbox(
                    repository
                        .findById(context.getOutboxId())
                        .map(entity -> entity.attach(this)));
    }

    protected CreateOutboxContext
    saveOutbox(CreateOutboxContext context)
    {
        logger.debug("<create> save Outbox");
        return
            context
                .setCreatedOutbox(
                    repository
                        .save(
                            context
                                .getCreatedOutbox()
                                .orElseThrow(
                                    () ->
                                        new IllegalStateException(
                                            "Error during outbox creation.")))
                        .notifyCreated());
    }

    protected UpdateOutboxContext
    saveOutbox(UpdateOutboxContext context)
    {
        logger.debug("<update> save Outbox");
        return
            context
                .setUpdatedOutbox(
                    repository
                        .save(
                            context
                                .getUpdatedOutbox()
                                .orElseThrow(
                                    () ->
                                        new IllegalStateException(
                                            "Error during outbox update.")))
                        .notifyUpdated());
    }

    protected UpdateOutboxContext
    updateOutbox(UpdateOutboxContext context)
    {
        UpdateOutboxData data = context.getOutboxToUpdate();

        logger.debug("update Outbox");

        data
            .getFoo()
            .ifPresent(
                foo ->
                    context
                        .getUpdatedOutbox()
                        .orElseThrow()
                        .setFoo(foo));

        return context;
    }

    protected DestroyOutboxContext
    deleteOutbox(DestroyOutboxContext context)
    {
        logger.debug("destroy Outbox");

        repository
            .delete(
                context
                    .getDestroyedOutbox()
                    .orElseThrow(
                        () ->
                            new NoSuchElementException(
                                "Error during outbox destroy")));

        context
            .getDestroyedOutbox()
            .ifPresent(Outbox::notifyDestroyed);

        return context;
    }

    private StartWorkerReply
    buildReply(CreateOutboxContext context)
    {
        Outbox created =
            context
                .getCreatedOutbox()
                .orElseThrow();

        logger.info("building successful reply");

        return
            new StartWorkerReply(context.getRequest())
                .setSuccess(true)
                .setSuccessMessage("Outbox: " + created.getPrimaryId() + " created")
                .setCreatedOutbox(mapper.map(created,OutboxData.class));
    }

    private StartWorkerReply
    buildReply(Throwable e,StartWorkerRequest request)
    {
        logger.info("building exception reply: " + e.getMessage());
        return
            new StartWorkerReply(request)
                .setSuccess(false)
                .setFailureMessage("Unable to create outbox")
                .setException(ExceptionData.of(e));
    }

    private StopWorkerReply
    buildReply(UpdateOutboxContext context)
    {
        Outbox created =
            context
                .getUpdatedOutbox()
                .orElseThrow();

        logger.info("building successful reply");

        return
            new StopWorkerReply(context.getRequest())
                .setSuccess(true)
                .setSuccessMessage("Outbox: " + created.getPrimaryId() + " created")
                .setUpdatedOutbox(mapper.map(created,OutboxData.class));
    }

    private StopWorkerReply
    buildReply(Throwable e,StopWorkerRequest request)
    {
        logger.info("building exception reply: " + e.getMessage());
        return
            new StopWorkerReply(request)
                .setSuccess(false)
                .setFailureMessage("Unable to create outbox")
                .setException(ExceptionData.of(e));
    }

    private QueryWorkerReply
    buildReply(DestroyOutboxContext context)
    {
        Outbox destroyed =
            context
                .getDestroyedOutbox()
                .orElseThrow();

        logger.info("building successful reply");

        return
            new QueryWorkerReply(context.getRequest())
                .setSuccess(true)
                .setSuccessMessage("Outbox: " + destroyed.getPrimaryId() + " destroyed")
                .setDestroyedOutbox(mapper.map(destroyed,OutboxData.class));
    }

    private QueryWorkerReply
    buildReply(Throwable e,QueryWorkerRequest request)
    {
        logger.info("building exception reply: " + e.getMessage());
        return
            new QueryWorkerReply(request)
                .setSuccess(false)
                .setFailureMessage("Unable to destroy outbox")
                .setException(ExceptionData.of(e));
    }

    private FindOutboxReply
    buildReply(FindOutboxContext context)
    {
        Outbox found =
            context
                .getFoundOutbox()
                .orElseThrow();

        logger.info("building successful reply");

        return
            new FindOutboxReply(context.getRequest())
                .setSuccess(true)
                .setSuccessMessage("Outbox: " + found.getPrimaryId() + " found")
                .setFoundOutbox(mapper.map(found,OutboxData.class));
    }

    private FindOutboxReply
    buildReply(Throwable e,FindOutboxRequest request)
    {
        logger.info("building exception reply: " + e.getMessage());
        return
            new FindOutboxReply(request)
                .setSuccess(false)
                .setFailureMessage("Unable to find outbox " + request.getOutboxId())
                .setException(ExceptionData.of(e));
    }

    private OutboxEvent
    toServiceEvent(IOutboxEvent event,StandardEventType eventType)
    {
        return
            OutboxEvent
                .newBuilder()
                .setEventType(eventType)
                .setIdentifiers(
                    EventIdentifiersData
                        .newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setCorrelationId(event.getCorrelationId())
                        .setTimestamp(event.getTimestamp())
                        .build())
                .setSourceId(
                    event
                        .getSource()
                        .getPrimaryId())
                .build();
    }
}

//////////////////////////////////////////////////////////////////////////////
