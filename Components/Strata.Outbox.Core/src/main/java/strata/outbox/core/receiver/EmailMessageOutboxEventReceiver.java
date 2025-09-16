/// ///////////////////////////////////////////////////////////////////////////
// EmailMessageOutboxEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.IEmailMessageSender;
import strata.server.core.notification.SerializableEmailMessage;

import java.io.IOException;

public
class EmailMessageOutboxEventReceiver
    extends AbstractOutboxEventReceiver<IEmailMessage>
    implements IOutboxEventReceiver
{
    private final IEmailMessageSender sender;
    private final Logger              logger;

    public
    EmailMessageOutboxEventReceiver(IEmailMessageSender sender)
    {
        super();
        this.sender = sender;
        this.logger = LogManager.getLogger(EmailMessageOutboxEventReceiver.class);
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
    protected IEmailMessage
    mapPayload(OutboxEvent event)
        throws MappingException
    {
        try
        {
            return
                getMapper()
                    .readValue(
                        event.getEventPayload(),
                        SerializableEmailMessage.class);
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
    processPayload(IEmailMessage payload)
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
                    "Failed to process IEmailMessage payload",e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
