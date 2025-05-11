/// ///////////////////////////////////////////////////////////////////////////
// OutboxEventServiceEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.sender;

import strata.foundation.core.event.CompletableSendResult;
import strata.foundation.core.event.ICompletableSendResult;
import strata.foundation.core.event.IEventSender;
import strata.foundation.core.event.SendResult;
import strata.outbox.core.repository.IOutboxEventRepository;

public abstract
class OutboxEventServiceEventSender<E,S extends IEventSender<E>>
    extends AbstractOutboxEventSender<E>
    implements IEventSender<E>
{

    protected
    OutboxEventServiceEventSender(
        ISourceToOutboxEventMapper<E> mapper,
        IOutboxEventRepository        repository)
    {
        super(mapper,repository);
    }

    protected
    OutboxEventServiceEventSender(
        ISourceToOutboxEventMapper<E> mapper,
        IOutboxEventRepository        repository,
        boolean                       autoDelete)
    {
        super(mapper,repository,autoDelete);
    }

    @Override
    public S
    open()
    {
        return getSelf();
    }

    @Override
    public S
    close()
    {
        return getSelf();
    }

    @Override
    public ICompletableSendResult<E>
    send(E e)
    {
        try
        {
            doSend(e);
            return CompletableSendResult.completedWith(new SendResult<>(e));
        }
        catch (Exception ex)
        {
            return CompletableSendResult.completedWith(new SendResult<>(ex));
        }
    }

    protected abstract S
    getSelf();
}

//////////////////////////////////////////////////////////////////////////////
