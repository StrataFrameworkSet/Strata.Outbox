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
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;

public abstract
class AbstractOutboxEventReceiver<T>
    implements IOutboxEventReceiver
{
    private final ObjectMapper mapper;

    protected
    AbstractOutboxEventReceiver()
    {
        this.mapper =
            new ObjectMapper()
                .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
                .enable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .enable(SerializationFeature.EAGER_SERIALIZER_FETCH)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new SimpleModule())
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());
    }

    @Override
    public void
    receive(OutboxEvent event)
        throws ReceiveException
    {
        try
        {
            T payload = mapPayload(event);

            processPayload(payload);
        }
        catch (MappingException e)
        {
            throw new ReceiveException("Failed to map outbox event", e);
        }
        catch (ReceiveException e)
        {
            throw e;
        }
        catch (Exception e)
        {
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
