/// ///////////////////////////////////////////////////////////////////////////
// DebeziumOutboxWorker.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.DebeziumEngine.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import strata.outbox.server.application.IOutboxWorker;

import java.io.IOException;
import java.util.concurrent.Executor;

@Service
public
class DebeziumOutboxWorker
    implements IOutboxWorker
{
    private final Builder<ChangeEvent<String,String>>  builder;
    private final IChangeEventProcessor                processor;
    private final Executor                             executor;
    private DebeziumEngine<ChangeEvent<String,String>> engine;
    private final Logger                               logger;

    public
    DebeziumOutboxWorker(
        Builder<ChangeEvent<String,String>> builder,
        IChangeEventProcessor               processor,
        Executor                            executor)
    {
        this.builder = builder;
        this.processor = processor;
        this.executor = executor;
        this.engine  = null;
        this.logger  = LogManager.getLogger(DebeziumOutboxWorker.class);
    }

    @Override
    public synchronized void
    start()
    {
        if (isWorking())
        {
            logger.warn("Outbox worker is already running");
            return;
        }

        logger.info("Starting outbox worker");

        DebeziumEngine<ChangeEvent<String,String>> started =
            builder
                .notifying(new DebeziumChangeConsumer(processor))
                .build();

        setEngine(started);
        executor.execute(() -> run(started));
    }

    private void
    run(DebeziumEngine<ChangeEvent<String,String>> started)
    {
        try
        {
            started.run();
        }
        finally
        {
            clearEngine(started);
        }
    }

    @Override
    public void
    stop()
    {
        if (!isWorking())
        {
            logger.warn("Outbox worker is not running");
            return;
        }

        try
        {
            logger.info("Stopping outbox worker");
            getEngine().close();
            setEngine(null);
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
        return getEngine() != null;
    }

    protected synchronized void
    setEngine(DebeziumEngine<ChangeEvent<String,String>> engine)
    {
        this.engine = engine;
    }

    protected synchronized DebeziumEngine<ChangeEvent<String,String>>
    getEngine()
    {
        return engine;
    }

    private synchronized void
    clearEngine(DebeziumEngine<ChangeEvent<String,String>> finished)
    {
        if (engine == finished)
            engine = null;
    }
}

//////////////////////////////////////////////////////////////////////////////
