/// ///////////////////////////////////////////////////////////////////////////
// ChangeEventToOutboxEventMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.outbox.core.shared.ObjectMapperProvider;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public
class ChangeEventToOutboxEventMapper
    implements IChangeEventToOutboxEventMapper
{
    private final ObjectMapper mapper;

    public
    ChangeEventToOutboxEventMapper()
    {
        this.mapper = new ObjectMapperProvider().get();
    }

    @Override
    public OutboxEvent
    map(ChangeEvent<String,String> event)
    {
        try
        {
            String value = event.value();
            Optional<JsonNode> root = Optional.ofNullable(mapper.readTree(value));
            Optional<JsonNode> envPayload = root.map(r -> r.get("payload"));
            Optional<JsonNode> after = envPayload.map(p -> p.path("after"));
            Optional<String> id = after.map(a -> a.get("id").asText());
            Optional<String> sourceType = after.map(a -> a.get("sourcetype").asText());
            Optional<String> sourceId = after.map(a -> a.get("sourceid").asText());
            Optional<String> type = after.map(a -> a.get("eventtype").asText());
            Optional<String> payload = after.map(a -> a.path("eventpayload").asText());

            root.orElseThrow(
                () -> new NoSuchElementException("root node not found"));
            envPayload.orElseThrow(
                () -> new NoSuchElementException("envelop payload not found"));
            after.orElseThrow(
                () -> new NoSuchElementException("after not found"));

            return
                new OutboxEvent()
                    .setId(
                        UUID.fromString(
                            id.orElseThrow(
                                () -> new NoSuchElementException("id not found"))))
                    .setSourceType(
                        sourceType.orElseThrow(
                            () -> new NoSuchElementException("sourceType not found")))
                    .setSourceId(
                        sourceId.orElseThrow(
                            () -> new NoSuchElementException("sourceId not found")))
                    .setEventType(
                        type.orElseThrow(
                            () -> new NoSuchElementException("eventType not found")))
                    .setEventPayload(
                        payload.orElseThrow(
                            () -> new NoSuchElementException("eventPayload not found")));
        }
        catch (Exception e)
        {
            throw new MappingException(e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
