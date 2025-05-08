/// ///////////////////////////////////////////////////////////////////////////
// AbstractOutboxEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.outbox.core.shared.ObjectMapperProvider;

public abstract
class AbstractOutboxEventReceiver<T>
    implements IOutboxEventReceiver
{
    private final ObjectMapper mapper;
    private final Logger       logger;

    protected
    AbstractOutboxEventReceiver()
    {
        this.mapper = new ObjectMapperProvider().get();
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public void
    receive(OutboxEvent event)
        throws ReceiveException
    {
        logger.info("Received outbox event {}", event.getId());
        try
        {
            T payload = mapPayload(event);

            processPayload(payload);
            logger.info(
                "Processed {} for outbox event {}",
                event.getEventType(),
                event.getId());
        }
        catch (MappingException e)
        {
            logger.error("Failed to map outbox event {}", event.getId(), e);
            throw new ReceiveException("Failed to map outbox event", e);
        }
        catch (ReceiveException e)
        {
            logger.error("Failed to process outbox event {}", event.getId(), e);
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Failed to process outbox event {}", event.getId(), e);
            throw new ReceiveException("Failed to process outbox event", e);
        }
    }

    protected ObjectMapper
    getMapper() { return mapper; }

    protected abstract T
    mapPayload(OutboxEvent event)
        throws MappingException;

    protected abstract void
    processPayload(T payload)
        throws ReceiveException;

}

//////////////////////////////////////////////////////////////////////////////
