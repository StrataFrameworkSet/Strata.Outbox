//////////////////////////////////////////////////////////////////////////////
// ModelMapperTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.server.domain.Outbox;
import strata.outbox.service.requestreply.OutboxData;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("CommitStage")
public
class ModelMapperTest
{
    private static ApplicationContext  context;
    private ModelMapper                mapper;

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
    setUp()
    {
        mapper = context.getBean(ModelMapper.class);
    }

    @AfterEach
    public void
    tearDown()
    {
        mapper = null;
    }

    @Test
    public void
    testOutboxDataToOutboxMapping()
    {
        OutboxData data =
            new OutboxData()
                .setOutboxId(1L)
                .setFoo("XXXYYYYYZZZZZZZ");
        Outbox entity = mapper.map(data,Outbox.class);

        assertEquals(data.getOutboxId(),entity.getPrimaryId());
        assertEquals(data.getFoo(),entity.getFoo());
    }

    @Test
    public void
    testOutboxToOutboxDataMapping()
    {
        Outbox entity =
            new Outbox()
                .setPrimaryId(1L)
                .setFoo("XXXXXXXXX.YYYYYYYYYYYYYYYYYY.ZZZZZZZZZZZZZZZZZZZ");

        OutboxData data = mapper.map(entity,OutboxData.class);

        assertEquals(entity.getPrimaryId(),data.getOutboxId());
        assertEquals(entity.getFoo(),data.getFoo());
    }

}

//////////////////////////////////////////////////////////////////////////////
