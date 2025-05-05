//////////////////////////////////////////////////////////////////////////////
// UpdateOutboxContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.server.domain.Outbox;
import strata.outbox.service.requestreply.UpdateOutboxData;
import strata.outbox.service.requestreply.UpdateOutboxRequest;

import java.util.Optional;

public
class UpdateOutboxContext
{
    private UpdateOutboxRequest request;
    private Optional<Outbox>    updatedOutbox;

    public
    UpdateOutboxContext(UpdateOutboxRequest req)
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

    public UpdateOutboxRequest
    getRequest() { return request; }

    public UpdateOutboxData
    getOutboxToUpdate() { return request.getOutbox(); }

    public Optional<Outbox>
    getUpdatedOutbox() { return updatedOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
