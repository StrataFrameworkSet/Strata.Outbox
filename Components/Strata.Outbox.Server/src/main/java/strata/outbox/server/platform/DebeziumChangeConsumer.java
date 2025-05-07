/// ///////////////////////////////////////////////////////////////////////////
// DebeziumChangeConsumer.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine.ChangeConsumer;
import io.debezium.engine.DebeziumEngine.RecordCommitter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.outbox.core.shared.ObjectMapperProvider;
import strata.outbox.server.domain.IOutboxEventRouter;

import java.util.List;
import java.util.Optional;

@Service
public
class DebeziumChangeConsumer
    implements ChangeConsumer<ChangeEvent<String,String>>
{
    private final ObjectMapper mapper;
    private final IOutboxEventRouter router;
    private final IOutboxEventRepository repository;
    private final Logger logger;

    public
    DebeziumChangeConsumer(
        IOutboxEventRouter     router,
        IOutboxEventRepository repository)
    {
        this.mapper = new ObjectMapperProvider().get();
        this.router = router;
        this.repository = repository;
        this.logger = LogManager.getLogger(DebeziumChangeConsumer.class);
    }

    @Override
    public void
    handleBatch(
        List<ChangeEvent<String,String>>            events,
        RecordCommitter<ChangeEvent<String,String>> committer)
        throws InterruptedException
    {
        events.forEach(event -> process(event, committer));
        committer.markBatchFinished();
    }

    @Transactional
    protected void
    process(
        ChangeEvent<String,String>                  event,
        RecordCommitter<ChangeEvent<String,String>> committer)
    {
        try
        {
            OutboxEvent outboxEvent = map(event);

            router.route(outboxEvent);
            committer.markProcessed(event);
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
        catch (InterruptedException e)
        {
            logger.error(e.getMessage(),e);
        }
    }

    protected OutboxEvent
    map(ChangeEvent<String,String> event)
        throws MappingException
    {
        try
        {
            Optional<JsonNode> root =
                Optional.ofNullable(mapper.readTree(event.value()));

            return new OutboxEvent();
        }
        catch (Exception e)
        {
            throw new MappingException(e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
