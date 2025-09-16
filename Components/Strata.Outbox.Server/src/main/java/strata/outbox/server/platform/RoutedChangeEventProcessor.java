/// ///////////////////////////////////////////////////////////////////////////
// RoutedChangeEventProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.outbox.server.domain.IOutboxEventRouter;

public
class RoutedChangeEventProcessor
    implements IChangeEventProcessor
{
    private final IChangeEventToOutboxEventMapper mapper;
    private final IOutboxEventRouter              router;
    private final IOutboxEventRepository          repository;
    private final Logger                          logger;
    private static final Integer                  MAX_ATTEMPTS = 5;

    public
    RoutedChangeEventProcessor(
        IOutboxEventRouter     router,
        IOutboxEventRepository repository)
    {
        this.mapper = new ChangeEventToOutboxEventMapper();
        this.router = router;
        this.repository = repository;
        this.logger = LogManager.getLogger(getClass());
    }

    @Override
    public void
    open()
        throws Exception
    {
        logger.info("Opening change event processor");
        router.open();
    }

    @Override
    public void
    close()
        throws Exception
    {
        logger.info("Closing change event processor");
        router.close();
    }

    @Override
    public void
    process(ChangeEvent<String,String> event)
    {
        logger.debug("Processing change event: {}", event.value());
        OutboxEvent outboxEvent = null;

        try
        {
            outboxEvent = mapper.map(event);

            logger.info(
                "Attempt {} for outbox event {}",
                outboxEvent.getAttempt(),
                outboxEvent);

            if (outboxEvent.getAttempt() > MAX_ATTEMPTS)
            {
                logger.error(
                    "Aborting outbox event {} after exceeding max attempts {}",
                    outboxEvent.getId(),
                    outboxEvent.getAttempt());
                return;
            }

            router.route(outboxEvent);
            repository
                .findById(outboxEvent.getId())
                .ifPresent(
                    e ->
                        {
                        logger.info("Deleting completed outbox event {}", e.getId());
                        repository.delete(e);
                        });
        }
        catch (MappingException e)
        {
            logger.error("Error mapping event",e);
        }
        catch (Exception ex)
        {
            logger.error("Error processing event",ex);
            logger.info(
                "Incrementing attempt for outbox event {} and re-saving",
                outboxEvent.getId());
            repository.save(outboxEvent.incrementAttempt());
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
