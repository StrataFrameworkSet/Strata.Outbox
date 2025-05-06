//////////////////////////////////////////////////////////////////////////////
// UpdateOutboxContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.StopWorkerRequest;

import java.util.Optional;

public
class UpdateOutboxContext
{
    private StopWorkerRequest request;
    private Optional<Outbox>    updatedOutbox;

    public
    UpdateOutboxContext(StopWorkerRequest req)
    {
        request = req;
        updatedOutbox = Optional.empty();
    }

    public UpdateOutboxContext
    setUpdatedOutbox(Optional<Outbox> updated)
    {
        updatedOutbox = updated;
        return this;
    }

    public UpdateOutboxContext
    setUpdatedOutbox(Outbox updated)
    {
        return setUpdatedOutbox(Optional.of(updated));
    }

    public StopWorkerRequest
    getRequest() { return request; }

    public UpdateOutboxData
    getOutboxToUpdate() { return request.getOutbox(); }

    public Optional<Outbox>
    getUpdatedOutbox() { return updatedOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
