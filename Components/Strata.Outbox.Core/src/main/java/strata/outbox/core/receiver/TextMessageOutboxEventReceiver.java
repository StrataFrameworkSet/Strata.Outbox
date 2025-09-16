//////////////////////////////////////////////////////////////////////////////
// EmailMessageOutboxEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.server.core.notification.*;

import java.io.IOException;

public
class TextMessageOutboxEventReceiver
    extends AbstractOutboxEventReceiver<ITextMessage>
    implements IOutboxEventReceiver
{
    private final ITextMessageSender sender;
    private final Logger             logger;

    public
    TextMessageOutboxEventReceiver(ITextMessageSender sender)
    {
        super();
        this.sender = sender;
        this.logger = LogManager.getLogger(TextMessageOutboxEventReceiver.class);
    }


    @Override
    public void
    open() throws ReceiveException
    {
        if (sender.isOpen())
        {
            logger.info("Event receiver is already open");
            return;
        }

        try
        {
            logger.info("Opening event receiver");
            sender.open();
        }
        catch (Exception e)
        {
            logger.error("Failed to open event receiver",e);
            throw new ReceiveException("Failed to open event receiver",e);
        }
    }

    @Override
    public void
    close()
        throws ReceiveException
    {
        if (!sender.isOpen())
        {
            logger.info("Event receiver is already closed");
            return;
        }

        try
        {
            logger.info("Closing event receiver");
            sender.close();
        }
        catch (Exception e)
        {
            logger.error("Failed to close event receiver",e);
            throw new ReceiveException("Failed to close event receiver",e);
        }
    }

    @Override
    public boolean
    isOpen()
    {
        return sender.isOpen();
    }

    @Override
    public boolean
    isClosed()
    {
        return !isOpen();
    }

    @Override
    protected ITextMessage
    mapPayload(OutboxEvent event)
        throws MappingException
    {
        try
        {
            return
                getMapper()
                    .readValue(
                        event.getEventPayload(),
                        SerializableTextMessage.class);
        }
        catch (IOException e)
        {
            throw
                new MappingException(
                    "Failed to map payload to IEmailMessage",e);
        }
    }

    @Override
    protected void
    processPayload(ITextMessage payload)
        throws ReceiveException
    {
        try
        {
            this.sender.send(payload);
        }
        catch (Exception e)
        {
            throw
                new ReceiveException(
                    "Failed to process ITextMessage payload",e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
