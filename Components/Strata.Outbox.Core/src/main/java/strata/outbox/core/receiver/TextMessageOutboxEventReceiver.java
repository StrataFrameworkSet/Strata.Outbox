//////////////////////////////////////////////////////////////////////////////
// EmailMessageOutboxEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

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

    public
    TextMessageOutboxEventReceiver(ITextMessageSender sender)
    {
        super();
        this.sender = sender;
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
