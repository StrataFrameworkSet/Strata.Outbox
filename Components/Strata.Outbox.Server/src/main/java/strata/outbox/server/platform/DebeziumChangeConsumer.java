/// ///////////////////////////////////////////////////////////////////////////
// DebeziumChangeConsumer.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine.RecordCommitter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public
class DebeziumChangeConsumer
    implements IChangeEventConsumer
{
    private final IChangeEventProcessor processor;
    private final Logger                logger;

    public
    DebeziumChangeConsumer(IChangeEventProcessor processor)
    {
        this.processor = processor;
        this.logger = LogManager.getLogger(DebeziumChangeConsumer.class);
    }

    @Override
    public void
    handleBatch(
        List<ChangeEvent<String,String>>            events,
        RecordCommitter<ChangeEvent<String,String>> committer)
        throws InterruptedException
    {
        logger.info("Processing batch of {} events", events.size());

        try
        {
            processor.open();

            events.forEach(
                event ->
                    {
                        try
                        {
                            processor.process(event);
                            committer.markProcessed(event);
                        }
                        catch (Throwable e)
                        {
                            logger.error("Error processing event: {}",event,e);
                        }
                    });
            committer.markBatchFinished();
        }
        catch (InterruptedException e)
        {
            logger.error("Batch processing interrupted",e);
            throw e;
        }
        catch (Throwable e)
        {
            logger.error("Error processing batch of events",e);
        }
        finally
        {
            try
            {
                processor.close();
            }
            catch (Exception e)
            {
                logger.error("Error closing processor",e);
            }
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
