/// ///////////////////////////////////////////////////////////////////////////
// OutboxEventEmailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.sender;

import strata.outbox.core.repository.IOutboxEventRepository;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.IEmailMessageSender;

public
class OutboxEventEmailMessageSender
    extends AbstractOutboxEventSender<IEmailMessage>
    implements IEmailMessageSender
{
    public
    OutboxEventEmailMessageSender(IOutboxEventRepository r,boolean auto)
    {
        super(new EmailMessageToOutboxEventMapper(),r,auto);
    }

    public
    OutboxEventEmailMessageSender(IOutboxEventRepository r)
    {
        super(new EmailMessageToOutboxEventMapper(),r);
    }

    @Override
    public IEmailMessageSender
    open()
    {
        return this;
    }

    @Override
    public IEmailMessageSender
    close()
    {
        return this;
    }

    @Override
    public IEmailMessageSender
    send(IEmailMessage message)
        throws SendException
    {
        try
        {
            this.doSend(message);
        }
        catch (Exception e)
        {
            throw new SendException("Error sending email message",e);
        }
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
