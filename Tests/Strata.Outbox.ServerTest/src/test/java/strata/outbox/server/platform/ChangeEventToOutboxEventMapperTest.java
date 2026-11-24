//////////////////////////////////////////////////////////////////////////////
// ChangeEventToOutboxEventMapperTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.repository.OutboxEventStatus;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class ChangeEventToOutboxEventMapperTest
{
    private static final String ID = "eca5ef6d-5d11-4fff-8e6a-b03632fa2db1";
    private static final String SOURCE_ID = "3370b11d-7c75-4e5c-a78c-ab69aa34912b";

    private IChangeEventToOutboxEventMapper subject;

    @BeforeEach
    public void
    setUp()
    {
        subject = new ChangeEventToOutboxEventMapper();
    }

    @Test
    public void
    testMapWithNullColumns()
    {
        Instant before = Instant.now();
        OutboxEvent actual =
            subject.map(
                createEvent("null","null","null","null"));

        assertEquals(UUID.fromString(ID),actual.getId());
        assertEquals("IEmailMessage",actual.getSourceType());
        assertEquals(SOURCE_ID,actual.getSourceId());
        assertEquals("IEmailMessage",actual.getEventType());
        assertEquals("{}",actual.getEventPayload());
        assertEquals(OutboxEventStatus.PENDING,actual.getStatus());
        assertNull(actual.getVersion());
        assertFalse(actual.getCreated().isBefore(before));
        assertFalse(actual.getLastModified().isBefore(before));
    }

    @Test
    public void
    testMapWithValues()
    {
        OutboxEvent actual =
            subject.map(
                createEvent(
                    "\"WORKING\"",
                    "3",
                    "\"2026-11-24T05:00:00Z\"",
                    "\"2026-11-24T06:00:00Z\""));

        assertEquals(OutboxEventStatus.WORKING,actual.getStatus());
        assertEquals(3L,actual.getVersion());
        assertEquals(Instant.parse("2026-11-24T05:00:00Z"),actual.getCreated());
        assertEquals(Instant.parse("2026-11-24T06:00:00Z"),actual.getLastModified());
    }

    @Test
    public void
    testMapWithEmptyStatus()
    {
        OutboxEvent actual =
            subject.map(createEvent("\"\"","null","null","null"));

        assertEquals(OutboxEventStatus.PENDING,actual.getStatus());
    }

    private ChangeEvent<String,String>
    createEvent(String status,String version,String created,String lastModified)
    {
        String value =
            "{\"payload\":{\"before\":null,\"after\":{" +
                "\"id\":\"" + ID + "\"," +
                "\"attempt\":1," +
                "\"eventpayload\":\"{}\"," +
                "\"eventtype\":\"IEmailMessage\"," +
                "\"sourceid\":\"" + SOURCE_ID + "\"," +
                "\"sourcetype\":\"IEmailMessage\"," +
                "\"created\":" + created + "," +
                "\"lastmodified\":" + lastModified + "," +
                "\"status\":" + status + "," +
                "\"version\":" + version +
            "}}}";

        return
            new ChangeEvent<>()
            {
                @Override
                public String
                key() { return null; }

                @Override
                public String
                value() { return value; }

                @Override
                public String
                destination() { return "outbox.public.outboxevent"; }

                @Override
                public Integer
                partition() { return null; }
            };
    }
}

//////////////////////////////////////////////////////////////////////////////
