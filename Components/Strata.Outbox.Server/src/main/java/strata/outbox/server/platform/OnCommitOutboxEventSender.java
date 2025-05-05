//////////////////////////////////////////////////////////////////////////////
// OnCommitOutboxEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import strata.outbox.service.event.IOutboxEventSender;
import strata.outbox.service.event.OutboxEvent;
import jakarta.inject.Inject;
import strata.foundation.core.event.IEventSender;
import strata.server.core.event.OnCommitEventSender;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;

public
class OnCommitOutboxEventSender
    extends OnCommitEventSender<OutboxEvent>
    implements IOutboxEventSender
{
    @Inject
    public
    OnCommitOutboxEventSender(
        IEventSender<OutboxEvent>     imp,
        IUnitOfWorkSynchronizationManager mgr)
    {
        super(imp,mgr);
    }
}

//////////////////////////////////////////////////////////////////////////////
