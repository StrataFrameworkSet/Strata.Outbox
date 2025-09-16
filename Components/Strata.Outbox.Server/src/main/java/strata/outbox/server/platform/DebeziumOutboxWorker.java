/// ///////////////////////////////////////////////////////////////////////////
// DebeziumOutboxWorker.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.DebeziumEngine.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import strata.outbox.server.application.IOutboxWorker;

import java.io.IOException;

@Service
public
class DebeziumOutboxWorker
    implements IOutboxWorker
{
    private final Builder<ChangeEvent<String,String>>  builder;
    private final IChangeEventProcessor                processor;
    private DebeziumEngine<ChangeEvent<String,String>> engine;
    private final Logger                               logger;

    public
    DebeziumOutboxWorker(
        Builder<ChangeEvent<String,String>> builder,
        IChangeEventProcessor               processor)
    {
        this.builder = builder;
        this.processor = processor;
        this.engine  = null;
        this.logger  = LogManager.getLogger(DebeziumOutboxWorker.class);
    }

    @Override
    @Async("outbox-worker")
    public void
    start()
    {
        if (isWorking())
        {
            logger.warn("Outbox worker is already running");
            return;
        }

        logger.info("Starting outbox worker");
        setEngine(
            builder
                .notifying(new DebeziumChangeConsumer(processor))
                .build());

        getEngine().run();
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
}

//////////////////////////////////////////////////////////////////////////////
