//////////////////////////////////////////////////////////////////////////////
// MockOutboxEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import strata.outbox.service.event.IOutboxEventSender;
import strata.outbox.service.event.OutboxEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.foundation.core.event.CompletableSendResult;
import strata.foundation.core.event.ICompletableSendResult;
import strata.foundation.core.event.IEventSender;
import strata.foundation.core.event.SendResult;

import java.util.concurrent.CompletableFuture;

public
class MockOutboxEventSender
    implements IOutboxEventSender
{
    private final Logger logger;

    public
    MockOutboxEventSender()
    {
        logger = LogManager.getLogger(MockOutboxEventSender.class);
    }

    @Override
    public IEventSender<OutboxEvent>
    open() throws Exception
    {
        logger.info("open()");
        return this;
    }

    @Override
    public IEventSender<OutboxEvent>
    close() throws Exception
    {
        logger.info("close()");
        return this;
    }

    @Override
    public ICompletableSendResult<OutboxEvent>
    send(OutboxEvent event)
    {
        logger.info("send(" + event.toString() + ")");
        return
            new CompletableSendResult<>(
                CompletableFuture.completedFuture(new SendResult<>(event)));
    }

    @Override
    public boolean
    isOpen()
    {
        return false;
    }

    @Override
    public boolean
    isClosed()
    {
        return false;
    }
}

//////////////////////////////////////////////////////////////////////////////
