//////////////////////////////////////////////////////////////////////////////
// AbstractOutboxEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.sender;

import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.core.repository.OutboxEvent;

public
class AbstractOutboxEventSender<T>
{
    private final ISourceToOutboxEventMapper<T> mapper;
    private final IOutboxEventRepository        repository;
    private boolean                             autoDelete;

    protected
    AbstractOutboxEventSender(
        ISourceToOutboxEventMapper<T> m,
        IOutboxEventRepository        r,
        boolean                       auto)
    {
        this.mapper     = m;
        this.repository = r;
        this.autoDelete = auto;
    }

    protected
    AbstractOutboxEventSender(ISourceToOutboxEventMapper<T> m,IOutboxEventRepository r)
    {
        this(m,r,false);
    }

    public boolean
    isOpen()
    {
        return this.mapper != null && this.repository != null;
    }

    public boolean
    isClosed()
    {
        return !this.isOpen();
    }

    protected void
    doSend(T source)
    {
        OutboxEvent outboxEvent = repository.save(mapper.map(source));

        if (this.autoDelete)
            this.repository.delete(outboxEvent);
    }
}

//////////////////////////////////////////////////////////////////////////////
