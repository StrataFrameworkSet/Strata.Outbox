/// ///////////////////////////////////////////////////////////////////////////
// DebeziumOutboxWorkerTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.shared.TestConfiguration;
import strata.server.core.notification.SerializableEmailMessage;
import strata.server.core.notification.SerializableTextMessage;

@Tag("IntegrationStage")
public
class DebeziumOutboxWorkerTest
{
    private static ApplicationContext context;
    private IOutboxWorker subject;
    private SerializableEmailMessage emailMessage;
    private SerializableTextMessage textMessage;
    private ObjectMapper mapper;

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
        subject = context.getBean(IOutboxWorker.class);
    }

}

//////////////////////////////////////////////////////////////////////////////
