/// ///////////////////////////////////////////////////////////////////////////
// EmailMessageOutboxEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

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

    public
    EmailMessageOutboxEventReceiver(IEmailMessageSender sender)
    {
        super();
        this.sender = sender;
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
