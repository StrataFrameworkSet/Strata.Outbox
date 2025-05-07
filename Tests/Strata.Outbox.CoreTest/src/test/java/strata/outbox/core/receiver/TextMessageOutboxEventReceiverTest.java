//////////////////////////////////////////////////////////////////////////////
// TextMessageOutboxEventReceiverTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.foundation.core.value.PhoneNumber;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MockTextMessageSender;
import strata.outbox.core.shared.ObjectMapperProvider;
import strata.server.core.notification.ITextMessage;
import strata.server.core.notification.SerializableTextMessage;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("CommitStage")
public
class TextMessageOutboxEventReceiverTest
{
    private MockTextMessageSender          sender;
    private TextMessageOutboxEventReceiver subject;
    private SerializableTextMessage        message;
    private String                         deserializedExpected;
    private ObjectMapper                   mapper;

    @BeforeEach
    public void
    setUp() throws JsonProcessingException
    {
        sender = new MockTextMessageSender();
        subject = new TextMessageOutboxEventReceiver(sender);
        message =
            new SerializableTextMessage()
                .setRecipients(Set.of(new PhoneNumber("+1234567890")))
                .setContent("Test Content");
        mapper = new ObjectMapperProvider().get();
        deserializedExpected = mapper.writeValueAsString(message);
    }

    @Test
    public void
    testReceive() throws Exception
    {
        OutboxEvent outboxEvent =
            new OutboxEvent()
                .setId(UUID.randomUUID())
                .setSourceType(
                    ITextMessage
                        .class
                        .getSimpleName())
                .setSourceId(UUID.randomUUID().toString())
                .setEventType(
                    ITextMessage
                        .class
                        .getSimpleName())
                .setEventPayload(mapper.writeValueAsString(message));

        subject.receive(outboxEvent);
        assertFalse(sender.getMessages().isEmpty());
        assertEquals(
            deserializedExpected,
            mapper.writeValueAsString(
                SerializableTextMessage.of(sender.getFirstMessage())));
    }
}

//////////////////////////////////////////////////////////////////////////////
