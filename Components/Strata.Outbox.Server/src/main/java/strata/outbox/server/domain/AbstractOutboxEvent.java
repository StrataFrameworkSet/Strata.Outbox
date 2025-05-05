//////////////////////////////////////////////////////////////////////////////
// AbstractOutboxEvent.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import strata.server.core.domainevent.AbstractDomainEvent;

public abstract
class AbstractOutboxEvent
    extends AbstractDomainEvent<Outbox>
    implements IOutboxEvent
{
    protected
    AbstractOutboxEvent(String nm,Outbox src)
    {
        super(nm,src);
    }

    protected
    AbstractOutboxEvent(String nm,String correlId,Outbox src)
    {
        super(nm,correlId,src);
    }
}

//////////////////////////////////////////////////////////////////////////////
