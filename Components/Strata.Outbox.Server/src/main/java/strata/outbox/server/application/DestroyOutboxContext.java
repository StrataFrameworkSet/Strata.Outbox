//////////////////////////////////////////////////////////////////////////////
// UpdateOutboxContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.server.domain.Outbox;
import strata.outbox.service.requestreply.DestroyOutboxRequest;

import java.util.Optional;

public
class DestroyOutboxContext
{
    private DestroyOutboxRequest request;
    private Optional<Outbox> destroyedOutbox;

    public
    DestroyOutboxContext(DestroyOutboxRequest req)
    {
        request = req;
        destroyedOutbox = Optional.empty();
    }

    public DestroyOutboxContext
    setDestroyedOutbox(Optional<Outbox> updated)
    {
        destroyedOutbox = updated;
        return this;
    }

    public DestroyOutboxContext
    setDestroyedOutbox(Outbox updated)
    {
        return setDestroyedOutbox(Optional.of(updated));
    }

    public DestroyOutboxRequest
    getRequest() { return request; }

    public Long
    getOutboxId() { return request.getOutboxId(); }

    public Optional<Outbox>
    getDestroyedOutbox() { return destroyedOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
