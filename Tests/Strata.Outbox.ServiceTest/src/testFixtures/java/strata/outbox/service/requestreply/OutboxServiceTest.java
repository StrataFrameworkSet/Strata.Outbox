//////////////////////////////////////////////////////////////////////////////
// OutboxServiceClientTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.outbox.service.requestreply.OutboxData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationStage")
public abstract
class OutboxServiceTest
{
    private IOutboxService subject;

    @Test
    public void
    testCreateOutbox()
    {
        CreateOutboxReply reply =
            getSubject()
                .createOutbox(
                    new CreateOutboxRequest()
                        .setOutbox(
                            new CreateOutboxData()
                                .setFoo("XXX-YYYYY-ZZZZZZZ")))
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
        Assertions.assertNotNull(reply.getCreatedOutbox());
        Assertions.assertNotNull(reply.getCreatedOutbox().getOutboxId());
        Assertions.assertEquals("XXX-YYYYY-ZZZZZZZ",reply.getCreatedOutbox().getFoo());
    }

    @Test
    public void
    testUpdateOutbox()
    {
        CreateOutboxReply createReply =
            getSubject()
                .createOutbox(
                    new CreateOutboxRequest()
                        .setOutbox(
                            new CreateOutboxData()
                                .setFoo("XXX-YYYYY-ZZZZZZZ")))
                .toCompletableFuture()
                .join();

        OutboxData entity = null;
        Long id = null;

        Assertions.assertTrue(createReply.isSuccess(),createReply.getFailureMessage());
        entity = createReply.getCreatedOutbox();
        id = entity.getOutboxId();
        Assertions.assertNotNull(entity);
        Assertions.assertNotNull(id);
        Assertions.assertEquals("XXX-YYYYY-ZZZZZZZ",entity.getFoo());

        UpdateOutboxReply updateReply =
            getSubject()
                .updateOutbox(
                    new UpdateOutboxRequest()
                        .setOutbox(
                            new UpdateOutboxData()
                                .setOutboxId(id)
                                .setFoo("AAA-BBBBB-CCCCCCC")))
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(updateReply.isSuccess(),updateReply.getFailureMessage());
        entity = updateReply.getUpdatedOutbox();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(id,entity.getOutboxId());
        Assertions.assertEquals("AAA-BBBBB-CCCCCCC",entity.getFoo());
    }

    @Test
    public void
    testDestroyOutbox()
    {
        CreateOutboxReply createReply =
            getSubject()
                .createOutbox(
                    new CreateOutboxRequest()
                        .setOutbox(
                            new CreateOutboxData()
                                .setFoo("XXX-YYYYY-ZZZZZZZ")))
                .toCompletableFuture()
                .join();

        OutboxData entity = null;
        Long id = null;

        Assertions.assertTrue(createReply.isSuccess(),createReply.getFailureMessage());
        entity = createReply.getCreatedOutbox();
        id = entity.getOutboxId();
        Assertions.assertNotNull(entity);
        Assertions.assertNotNull(id);
        Assertions.assertEquals("XXX-YYYYY-ZZZZZZZ",entity.getFoo());

        DestroyOutboxReply destroyReply =
            getSubject()
                .destroyOutbox(
                    new DestroyOutboxRequest()
                        .setOutboxId(id))
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(destroyReply.isSuccess(),destroyReply.getFailureMessage());
        entity = destroyReply.getDestroyedOutbox();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(id,entity.getOutboxId());
    }

    @Test
    public void
    testFindOutbox()
    {
        CreateOutboxReply createReply =
            getSubject()
                .createOutbox(
                    new CreateOutboxRequest()
                        .setOutbox(
                            new CreateOutboxData()
                                .setFoo("XXX-YYYYY-ZZZZZZZ")))
                .toCompletableFuture()
                .join();

        OutboxData entity = null;
        Long id = null;

        Assertions.assertTrue(createReply.isSuccess(),createReply.getFailureMessage());
        entity = createReply.getCreatedOutbox();
        id = entity.getOutboxId();
        Assertions.assertNotNull(entity);
        Assertions.assertNotNull(id);
        Assertions.assertEquals("XXX-YYYYY-ZZZZZZZ",entity.getFoo());

        FindOutboxReply findReply =
            getSubject()
                .findOutbox(
                    new FindOutboxRequest()
                        .setOutboxId(id))
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(findReply.isSuccess(),findReply.getFailureMessage());
        entity = findReply.getFoundOutbox();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(id,entity.getOutboxId());
        Assertions.assertEquals("XXX-YYYYY-ZZZZZZZ",entity.getFoo());
    }

    protected abstract IOutboxService
    getSubject();
}

//////////////////////////////////////////////////////////////////////////////
