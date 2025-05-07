//////////////////////////////////////////////////////////////////////////////
// OutboxServiceClientTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.server.shared.TestConfiguration;
import strata.outbox.service.requestreply.IOutboxService;
import strata.outbox.service.requestreply.OutboxServiceTest;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import strata.server.spring.inject.RequestAttributeMap;

@Tag("CommitStage")
public
class OutboxServiceServerTest
    extends OutboxServiceTest
{
    private static ApplicationContext context;
    private IOutboxService            subject;

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
    setUp() throws Exception
    {
        RequestContextHolder.setRequestAttributes(new RequestAttributeMap());
        subject = context.getBean(IOutboxService.class);
    }

    @AfterEach
    public void
    tearDown()
    {
        subject = null;
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void
    testStartWorker()
    {
        super.testStartWorker();
    }

    @Test
    public void
    testStopWorker()
    {
        super.testStopWorker();
    }

    @Test
    public void
    testQueryWorker()
    {
        super.testQueryWorker();
    }

    @Override
    protected IOutboxService
    getSubject()
    {
        return subject;
    }

}

//////////////////////////////////////////////////////////////////////////////
