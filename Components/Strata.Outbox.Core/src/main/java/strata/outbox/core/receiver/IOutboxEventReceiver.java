//////////////////////////////////////////////////////////////////////////////
// IOutboxEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

import strata.outbox.core.repository.OutboxEvent;

public
interface IOutboxEventReceiver
{
    void
    open()
        throws ReceiveException;

    void
    close()
        throws ReceiveException;

    boolean
    isOpen();

    boolean
    isClosed();

    void
    receive(OutboxEvent event)
        throws ReceiveException;
}

//////////////////////////////////////////////////////////////////////////////