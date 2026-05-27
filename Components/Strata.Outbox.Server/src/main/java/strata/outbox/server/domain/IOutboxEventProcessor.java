//////////////////////////////////////////////////////////////////////////////
// IOutboxEventProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import jakarta.transaction.Transactional;
import strata.outbox.core.repository.OutboxEvent;

import java.util.List;

@Transactional
public
interface IOutboxEventProcessor
{
    List<OutboxEvent>
    getPending();

    OutboxEvent
    checkOut(OutboxEvent pending);

    void
    processEvent(OutboxEvent event);
}

//////////////////////////////////////////////////////////////////////////////