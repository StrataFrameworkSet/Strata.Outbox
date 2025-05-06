/// ///////////////////////////////////////////////////////////////////////////
// DebeziumOutboxWorker.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.DebeziumEngine.Builder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.IOutboxEventRouter;

import java.io.IOException;

@Service
public
class DebeziumOutboxWorker
    implements IOutboxWorker
{
    private final Builder<ChangeEvent<String,String>>  builder;
    private DebeziumEngine<ChangeEvent<String,String>> engine;
    private final IOutboxEventRouter                   router;

    public
    DebeziumOutboxWorker(
        Builder<ChangeEvent<String,String>> builder,
        IOutboxEventRouter                         router)
    {
        this.builder = builder;
        this.engine  = null;
        this.router  = router;
    }

    @Override
    @Async("executor")
    public void
    start()
    {
        if (isWorking())
            return;

        engine =
            builder
            .notifying(new DebeziumChangeConsumer(router))
            .build();

        engine.run();
    }

    @Override
    public void
    stop()
    {
        if (!isWorking())
            return;

        try
        {
            engine.close();
            engine = null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean
    isWorking()
    {
        return engine != null;
    }
}

//////////////////////////////////////////////////////////////////////////////
