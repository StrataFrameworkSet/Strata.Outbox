/// ///////////////////////////////////////////////////////////////////////////
// OutboxEventRouterTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import strata.foundation.core.value.EmailAddress;
import strata.foundation.core.value.PhoneNumber;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.ObjectMapperProvider;
import strata.outbox.server.shared.TestConfiguration;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.ITextMessage;
import strata.server.core.notification.SerializableEmailMessage;
import strata.server.core.notification.SerializableTextMessage;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class OutboxEventRouterTest
{
    private static ApplicationContext context;
    private IOutboxEventRouter        subject;
    private SerializableEmailMessage  emailMessage;
    private SerializableTextMessage   textMessage;
    private ObjectMapper              mapper;

    @BeforeAll
    public static void
    initialize()
    {
        context =
            new AnnotationConfigApplicationContext(
                TestConfiguration.class);
    }

    @BeforeEach
    public void
    setup()
    {
        subject = context.getBean(IOutboxEventRouter.class);
        emailMessage =
            new SerializableEmailMessage()
                .setSender(new EmailAddress("johnliebenau@gmail.com"))
                .setRecipients(Set.of(new EmailAddress("johnliebenau@gmail.com")))
                .setSubject("Test Subject")
                .setContent("Test Content");
        textMessage =
            new SerializableTextMessage()
                .setRecipients(Set.of(new PhoneNumber("+1234567890")))
                .setContent("Test Content");

        mapper = new ObjectMapperProvider().get();
    }

    @Test
    public void
    testRoute() throws Exception
    {
        // Arrange
        OutboxEvent emailEvent =
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
                .setEventPayload(mapper.writeValueAsString(emailMessage));
        OutboxEvent textEvent =
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
                .setEventPayload(mapper.writeValueAsString(textMessage));

        OutboxEvent unknownEvent = new OutboxEvent().setEventType("UnknownEvent");

        // Act & Assert
        // Test successful routing of known event types
        assertDoesNotThrow(() -> subject.route(emailEvent));
        assertDoesNotThrow(() -> subject.route(textEvent));

        // Test exception is thrown for unknown event type
        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> subject.route(unknownEvent));

        assertEquals(
            "No receiver found for event type: UnknownEvent",
            exception.getMessage());
    }
}

//////////////////////////////////////////////////////////////////////////////
