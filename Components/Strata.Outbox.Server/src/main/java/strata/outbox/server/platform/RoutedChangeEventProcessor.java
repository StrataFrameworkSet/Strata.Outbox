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
    process(ChangeEvent<String,String> event)
    {
        logger.debug("Processing change event: {}", event.value());
        try
        {
            OutboxEvent outboxEvent = mapper.map(event);

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
        catch (Exception e)
        {
            logger.error("Error processing event",e);
        }

    }
}

//////////////////////////////////////////////////////////////////////////////
