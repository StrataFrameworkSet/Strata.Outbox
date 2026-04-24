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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

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
    testIncrementAndResave() throws Exception
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

        transaction.executeWithoutResult(
            status ->
                {
                actual.set(target.save(expected.get().incrementAttempt()));
                });
        assertNotNull(actual.get());
        assertEquals(
            2,
            actual.get().getAttempt());

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

    @Test
    public void
    testStatusDefaultsToPendingOnSave() throws Exception
    {
        final AtomicReference<OutboxEvent> actual = new AtomicReference<>();

        OutboxEvent toSave =
            new OutboxEvent()
                .setSourceId(UUID.randomUUID().toString())
                .setSourceType(IEmailMessage.class.getSimpleName())
                .setEventType(IEmailMessage.class.getSimpleName())
                .setEventPayload(
                    mapper.writeValueAsString(
                        new SerializableEmailMessage()
                            .setSender(new EmailAddress("johnliebenau@gmail.com"))
                            .setRecipients(
                                Set.of(new EmailAddress("johnliebenau@gmail.com")))
                            .setSubject("Test Subject")
                            .setContent("This is a test.")));

        assertEquals(OutboxEventStatus.PENDING,toSave.getStatus());

        transaction.executeWithoutResult(
            status -> actual.set(target.save(toSave)));

        assertNotNull(actual.get());
        assertNotNull(actual.get().getId());
        assertEquals(OutboxEventStatus.PENDING,actual.get().getStatus());
    }

    @Test
    public void
    testStatusIsPersistedAndRetrieved() throws Exception
    {
        final AtomicReference<OutboxEvent> saved = new AtomicReference<>();

        OutboxEvent toSave =
            new OutboxEvent()
                .setSourceId(UUID.randomUUID().toString())
                .setSourceType(IEmailMessage.class.getSimpleName())
                .setEventType(IEmailMessage.class.getSimpleName())
                .setEventPayload(
                    mapper.writeValueAsString(
                        new SerializableEmailMessage()
                            .setSender(new EmailAddress("johnliebenau@gmail.com"))
                            .setRecipients(
                                Set.of(new EmailAddress("johnliebenau@gmail.com")))
                            .setSubject("Test Subject")
                            .setContent("This is a test.")))
                .setStatus(OutboxEventStatus.WORKING);

        transaction.executeWithoutResult(
            status -> saved.set(target.save(toSave)));

        OutboxEvent reloaded =
            transaction.execute(
                status ->
                    target
                        .findById(saved.get().getId())
                        .orElseThrow());

        assertNotNull(reloaded);
        assertEquals(OutboxEventStatus.WORKING,reloaded.getStatus());
    }

    @Test
    public void
    testFindAllByStatusReturnsOnlyMatchingEvents() throws Exception
    {
        final AtomicReference<OutboxEvent> pendingRef = new AtomicReference<>();
        final AtomicReference<OutboxEvent> workingRef = new AtomicReference<>();
        final AtomicReference<OutboxEvent> completedRef = new AtomicReference<>();

        String payload =
            mapper.writeValueAsString(
                new SerializableEmailMessage()
                    .setSender(new EmailAddress("johnliebenau@gmail.com"))
                    .setRecipients(
                        Set.of(new EmailAddress("johnliebenau@gmail.com")))
                    .setSubject("Test Subject")
                    .setContent("This is a test."));

        transaction.executeWithoutResult(
            status ->
                {
                    pendingRef.set(
                        target.save(
                            new OutboxEvent()
                                .setSourceId(UUID.randomUUID().toString())
                                .setSourceType(IEmailMessage.class.getSimpleName())
                                .setEventType(IEmailMessage.class.getSimpleName())
                                .setEventPayload(payload)
                                .setStatus(OutboxEventStatus.PENDING)));
                    workingRef.set(
                        target.save(
                            new OutboxEvent()
                                .setSourceId(UUID.randomUUID().toString())
                                .setSourceType(IEmailMessage.class.getSimpleName())
                                .setEventType(IEmailMessage.class.getSimpleName())
                                .setEventPayload(payload)
                                .setStatus(OutboxEventStatus.WORKING)));
                    completedRef.set(
                        target.save(
                            new OutboxEvent()
                                .setSourceId(UUID.randomUUID().toString())
                                .setSourceType(IEmailMessage.class.getSimpleName())
                                .setEventType(IEmailMessage.class.getSimpleName())
                                .setEventPayload(payload)
                                .setStatus(OutboxEventStatus.COMPLETED)));
                });

        List<OutboxEvent> pending =
            transaction.execute(
                status -> target.findAllByStatus(OutboxEventStatus.PENDING));
        List<OutboxEvent> working =
            transaction.execute(
                status -> target.findAllByStatus(OutboxEventStatus.WORKING));
        List<OutboxEvent> completed =
            transaction.execute(
                status -> target.findAllByStatus(OutboxEventStatus.COMPLETED));

        assertNotNull(pending);
        assertNotNull(working);
        assertNotNull(completed);

        assertTrue(
            pending
                .stream()
                .anyMatch(e -> e.getId().equals(pendingRef.get().getId())));
        assertTrue(
            pending
                .stream()
                .allMatch(e -> e.getStatus() == OutboxEventStatus.PENDING));

        assertTrue(
            working
                .stream()
                .anyMatch(e -> e.getId().equals(workingRef.get().getId())));
        assertTrue(
            working
                .stream()
                .allMatch(e -> e.getStatus() == OutboxEventStatus.WORKING));

        assertTrue(
            completed
                .stream()
                .anyMatch(e -> e.getId().equals(completedRef.get().getId())));
        assertTrue(
            completed
                .stream()
                .allMatch(e -> e.getStatus() == OutboxEventStatus.COMPLETED));
    }

    @Test
    public void
    testFindAllByStatusReturnsEmptyWhenNoMatches() throws Exception
    {
        // Use a sentinel that we don't save anywhere else in this test. Since
        // every saved event in this method uses PENDING, COMPLETED should
        // (at minimum) not contain any of our newly created events.
        final AtomicReference<OutboxEvent> savedRef = new AtomicReference<>();

        OutboxEvent toSave =
            new OutboxEvent()
                .setSourceId(UUID.randomUUID().toString())
                .setSourceType(IEmailMessage.class.getSimpleName())
                .setEventType(IEmailMessage.class.getSimpleName())
                .setEventPayload(
                    mapper.writeValueAsString(
                        new SerializableEmailMessage()
                            .setSender(new EmailAddress("johnliebenau@gmail.com"))
                            .setRecipients(
                                Set.of(new EmailAddress("johnliebenau@gmail.com")))
                            .setSubject("Test Subject")
                            .setContent("This is a test.")))
                .setStatus(OutboxEventStatus.PENDING);

        transaction.executeWithoutResult(
            status -> savedRef.set(target.save(toSave)));

        List<OutboxEvent> completed =
            transaction.execute(
                status -> target.findAllByStatus(OutboxEventStatus.COMPLETED));

        assertNotNull(completed);
        assertTrue(
            completed
                .stream()
                .noneMatch(e -> e.getId().equals(savedRef.get().getId())));
    }

}

//////////////////////////////////////////////////////////////////////////////
