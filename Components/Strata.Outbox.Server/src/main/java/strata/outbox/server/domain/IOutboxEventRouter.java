//////////////////////////////////////////////////////////////////////////////
// IOutboxEventRouter.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import strata.outbox.core.receiver.ReceiveException;
import strata.outbox.core.repository.OutboxEvent;

public
interface IOutboxEventRouter
{
    void
    open()
        throws ReceiveException;

    void
    close()
        throws ReceiveException;

    void
    route(OutboxEvent event);
}

//////////////////////////////////////////////////////////////////////////////