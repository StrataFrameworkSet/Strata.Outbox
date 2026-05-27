//////////////////////////////////////////////////////////////////////////////
// OutboxEventProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.repository.OutboxEventStatus;
import strata.outbox.server.domain.IOutboxEventProcessor;
import strata.outbox.server.domain.IOutboxEventRouter;

import java.time.Instant;
import java.util.List;

public
class OutboxEventProcessor
    implements IOutboxEventProcessor
{
    private final IOutboxEventRepository repository;
    private final IOutboxEventRouter     router;
    private final Logger                 logger;

    private static final Integer         MAX_ATTEMPTS = 5;
    private static final int             BATCH_SIZE = 100;

    @Inject
    public OutboxEventProcessor(
        IOutboxEventRepository repository,
        IOutboxEventRouter router)
    {
        this.repository = repository;
        this.router = router;
        this.logger = LogManager.getLogger(getClass());
    }

    @Override
    public List<OutboxEvent>
    getPending()
    {
        return repository.findAllByStatusOrderByCreated(OutboxEventStatus.PENDING,BATCH_SIZE);
    }

    @Override
    public OutboxEvent
    checkOut(OutboxEvent pending)
    {
        return repository.save(
            pending.setStatus(OutboxEventStatus.WORKING)
                   .setLastModified(Instant.now()));
    }


    @Override
    public void
    processEvent(OutboxEvent event)
    {
        try
        {
            logger.info(
                "Attempt {} for outbox event {}",
                event.getAttempt(),
                event.getId());

            if (event.getAttempt() > MAX_ATTEMPTS)
            {
                logger.error(
                    "Aborting outbox event {} after exceeding max attempts {}",
                    event.getId(),
                    event.getAttempt());
                repository.save(
                    event.setStatus(OutboxEventStatus.FAILED)
                         .setLastModified(Instant.now()));
                return;
            }

            router.route(event);
            logger.info("Deleting completed outbox event {}",event.getId());
            repository.delete(event);
        }
        catch (Exception ex)
        {
            logger.error("Error processing outbox event {}",event.getId(),ex);
            logger.info(
                "Incrementing attempt for outbox event {} and re-saving",
                event.getId());
            repository.save(
                event.setStatus(OutboxEventStatus.PENDING)
                     .incrementAttempt()
                     .setLastModified(Instant.now()));
        }
    }

}

//////////////////////////////////////////////////////////////////////////////
