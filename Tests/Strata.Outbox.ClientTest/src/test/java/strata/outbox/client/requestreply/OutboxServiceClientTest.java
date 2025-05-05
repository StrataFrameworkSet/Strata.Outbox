//////////////////////////////////////////////////////////////////////////////
// OutboxServiceClientTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.client.requestreply;

import strata.outbox.service.requestreply.IOutboxService;
import strata.outbox.service.requestreply.OutboxServiceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationStage")
public
class OutboxServiceClientTest
    extends OutboxServiceTest
{
    private IOutboxServiceClient subject;

    @BeforeEach
    public void
    setUp() throws Exception
    {
        subject = new OutboxServiceClient("https://localhost:8081");
    }

    @AfterEach
    public void
    tearDown()
    {
        subject.close();
        subject = null;
    }

    @Override
    @Test
    public void
    testCreateOutbox()
    {
        super.testCreateOutbox();
    }

    @Override
    @Test
    public void
    testUpdateOutbox()
    {
        super.testUpdateOutbox();
    }

    @Override
    @Test
    public void
    testDestroyOutbox()
    {
        super.testDestroyOutbox();
    }

    @Override
    @Test
    public void
    testFindOutbox()
    {
        super.testFindOutbox();
    }

    @Override
    protected IOutboxService
    getSubject()
    {
        return subject;
    }

}

//////////////////////////////////////////////////////////////////////////////
