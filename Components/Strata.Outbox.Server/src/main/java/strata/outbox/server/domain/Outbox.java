//////////////////////////////////////////////////////////////////////////////
// Outbox.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import strata.server.core.entity.AbstractEntityAndDomainEventSource;

public
class Outbox
    extends
        AbstractEntityAndDomainEventSource<
            Long,
            Outbox,
            IOutboxEvent,
            IOutboxEventObserver>
    implements IOutboxEventSource
{
    private String foo;

    public
    Outbox()
    {
        super();
        foo = null;
    }

    @Override
    public Outbox
    setPrimaryId(Long primaryId)
    {
        return super.setPrimaryId(primaryId);
    }

    @Override
    public Long
    getPrimaryId()
    {
        return super.getPrimaryId();
    }

    public Outbox
    setFoo(String f)
    {
        foo = f;
        return this;
    }

    public String
    getFoo() { return foo; }

    public Outbox
    notifyCreated() { return super.notify(new OutboxCreated(this)); }

    public Outbox
    notifyUpdated() { return super.notify(new OutboxUpdated(this)); }

    public Outbox
    notifyDestroyed() { return super.notify(new OutboxDestroyed(this)); }

    @Override
    protected Outbox
    getSelf()
    {
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
