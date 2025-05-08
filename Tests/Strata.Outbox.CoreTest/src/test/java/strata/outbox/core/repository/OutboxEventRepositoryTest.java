//////////////////////////////////////////////////////////////////////////////
// OutboxEventRepositoryTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import strata.foundation.core.value.EmailAddress;
import strata.outbox.core.shared.ObjectMapperProvider;
import strata.outbox.core.shared.TestConfiguration;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.SerializableEmailMessage;
import strata.server.spring.inject.RequestAttributeMap;
import strata.server.spring.unitofwork.ISpringUnitOfWorkManager;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("CommitStage")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public
class OutboxEventRepositoryTest
{
    private RequestAttributeMap requestAttributes;

    @Autowired
    private IOutboxEventRepository target;

    @Autowired
    private ISpringUnitOfWorkManager unitOfWorkManager;

    private ObjectMapper mapper;

    private TransactionTemplate transaction;

    @BeforeAll
    public static void
    init() { System.setProperty("log4j.configuration", "log4j.properties"); }

    @BeforeEach
    public void
    setUp() throws Exception
    {
        RequestContextHolder.setRequestAttributes(new RequestAttributeMap());
        mapper = new ObjectMapperProvider().get();
        transaction = new TransactionTemplate(unitOfWorkManager);
    }

    @AfterEach
    public void
    tearDown()
    {
        target = null;
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void
    testSave() throws Exception
    {
        final AtomicReference<OutboxEvent> expected = new AtomicReference<>();
        final AtomicReference<OutboxEvent> actual = new AtomicReference<>();

        expected.set(
            new OutboxEvent()
                .setSourceId(
                    UUID
                        .randomUUID()
                        .toString())
                .setSourceType(
                    IEmailMessage
                        .class
                        .getSimpleName())
                .setEventType(
                    IEmailMessage
                        .class
                        .getSimpleName())
                .setEventPayload(
                    mapper.writeValueAsString(
                        new SerializableEmailMessage()
                            .setSender(new EmailAddress("johnliebenau@gmail.com"))
                            .setRecipients(
                                Set.of(new EmailAddress("johnliebenau@gmail.com")))
                            .setSubject("Test Subject")
                            .setContent("This is a test."))));

        transaction.executeWithoutResult(
            status ->
            {
                actual.set(target.save(expected.get()));
            });
        assertNotNull(actual.get());
        assertNotNull(actual.get().getId());
    }

    @Test
    public void
    testDelete() throws Exception
    {
        final AtomicReference<OutboxEvent> expected = new AtomicReference<>();
        final AtomicReference<OutboxEvent> actual = new AtomicReference<>();

        expected.set(
            new OutboxEvent()
                .setSourceId(
                    UUID
                        .randomUUID()
                        .toString())
                .setSourceType(
                    IEmailMessage
                        .class
                        .getSimpleName())
                .setEventType(
                    IEmailMessage
                        .class
                        .getSimpleName())
                .setEventPayload(
                    mapper.writeValueAsString(
                        new SerializableEmailMessage()
                            .setSender(new EmailAddress("johnliebenau@gmail.com"))
                            .setRecipients(
                                Set.of(new EmailAddress("johnliebenau@gmail.com")))
                            .setSubject("Test Subject")
                            .setContent("This is a test."))));

        transaction.executeWithoutResult(
            status ->
                {
                actual.set(target.save(expected.get()));
                });
        assertNotNull(actual.get());

        transaction.executeWithoutResult(
            status ->
            {
                target.delete(actual.get());
            });

        assertFalse(
            (boolean)
                transaction
                    .execute(
                        status ->
                            target.existsById(actual.get().getId())));

    }


    @Test
    public void
    testSaveMultiple() throws Exception
    {
        final AtomicReference<OutboxEvent> expected = new AtomicReference<>();
        final AtomicReference<OutboxEvent> actual = new AtomicReference<>();

        expected.set(
            new OutboxEvent()
                .setSourceId(
                    UUID
                        .randomUUID()
                        .toString())
                .setSourceType(
                    IEmailMessage
                        .class
                        .getSimpleName())
                .setEventType(
                    IEmailMessage
                        .class
                        .getSimpleName())
                .setEventPayload(
                    mapper.writeValueAsString(
                        new SerializableEmailMessage()
                            .setSender(new EmailAddress("johnliebenau@gmail.com"))
                            .setRecipients(
                                Set.of(new EmailAddress("johnliebenau@gmail.com")))
                            .setSubject("Test Subject")
                            .setContent("This is a test."))));

        transaction.executeWithoutResult(
            status ->
                {
                    for (int i=0;i<5;i++)
                        target.save(expected.get());
                });

    }

}

//////////////////////////////////////////////////////////////////////////////
