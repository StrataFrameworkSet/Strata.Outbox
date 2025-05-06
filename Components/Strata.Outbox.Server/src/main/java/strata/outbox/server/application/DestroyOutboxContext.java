//////////////////////////////////////////////////////////////////////////////
// UpdateOutboxContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.QueryWorkerRequest;

import java.util.Optional;

public
class DestroyOutboxContext
{
    private QueryWorkerRequest request;
    private Optional<Outbox> destroyedOutbox;

    public
    DestroyOutboxContext(QueryWorkerRequest req)
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

    public QueryWorkerRequest
    getRequest() { return request; }

    public Long
    getOutboxId() { return request.getOutboxId(); }

    public Optional<Outbox>
    getDestroyedOutbox() { return destroyedOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
