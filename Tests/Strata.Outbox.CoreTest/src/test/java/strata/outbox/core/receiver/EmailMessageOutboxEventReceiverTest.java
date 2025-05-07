//////////////////////////////////////////////////////////////////////////////
// EmailMessageOutboxEventReceiverTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.foundation.core.value.EmailAddress;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MockEmailMessageSender;
import strata.outbox.core.shared.ObjectMapperProvider;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.SerializableEmailMessage;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class EmailMessageOutboxEventReceiverTest
{
    private MockEmailMessageSender          sender;
    private EmailMessageOutboxEventReceiver subject;
    private SerializableEmailMessage        message;
    private String                          deserializedExpected;
    private ObjectMapper                    mapper;

    @BeforeEach
    public void
    setUp() throws JsonProcessingException
    {
        sender = new MockEmailMessageSender();
        subject = new EmailMessageOutboxEventReceiver(sender);
        message =
            new SerializableEmailMessage()
                .setSender(new EmailAddress("johnliebenau@gmail.com"))
                .setRecipients(Set.of(new EmailAddress("johnliebenau@gmail.com")))
                .setSubject("Test Subject")
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
                    IEmailMessage
                        .class
                        .getSimpleName())
                .setSourceId(UUID.randomUUID().toString())
                .setEventType(
                    IEmailMessage
                        .class
                        .getSimpleName())
                .setEventPayload(mapper.writeValueAsString(message));

        subject.receive(outboxEvent);
        assertFalse(sender.getMessages().isEmpty());
        assertEquals(
            deserializedExpected,
            mapper.writeValueAsString(
                SerializableEmailMessage.of(sender.getFirstMessage())));
    }
}

//////////////////////////////////////////////////////////////////////////////
