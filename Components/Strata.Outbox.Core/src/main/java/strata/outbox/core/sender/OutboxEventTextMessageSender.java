//////////////////////////////////////////////////////////////////////////////
// OutboxEventEmailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.sender;

import strata.outbox.core.repository.IOutboxEventRepository;
import strata.server.core.notification.ITextMessage;
import strata.server.core.notification.ITextMessageSender;

public
class OutboxEventTextMessageSender
    extends AbstractOutboxEventSender<ITextMessage>
    implements ITextMessageSender
{
    public
    OutboxEventTextMessageSender(IOutboxEventRepository r,boolean auto)
    {
        super(new TextMessageToOutboxEventMapper(),r,auto);
    }

    public
    OutboxEventTextMessageSender(IOutboxEventRepository r)
    {
        super(new TextMessageToOutboxEventMapper(),r);
    }

    @Override
    public ITextMessageSender
    open()
    {
        return this;
    }

    @Override
    public ITextMessageSender
    close()
    {
        return this;
    }

    @Override
    public ITextMessageSender
    send(ITextMessage message)
        throws SendException
    {
        try
        {
            this.doSend(message);
        }
        catch (Exception e)
        {
            throw new SendException("Error sending text message",e);
        }
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
