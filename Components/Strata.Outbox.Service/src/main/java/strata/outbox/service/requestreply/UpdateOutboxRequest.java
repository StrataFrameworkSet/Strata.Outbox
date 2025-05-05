//////////////////////////////////////////////////////////////////////////////
// UpdateOutboxRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;

import java.time.Instant;
import java.util.UUID;

public
class UpdateOutboxRequest
    extends AbstractServiceRequest
{
    private UpdateOutboxData outbox;
    private String foo;

    public
    UpdateOutboxRequest()
    {
        super();
        outbox = null;
        foo = null;
    }

    @Override
    public UpdateOutboxRequest
    setRequestId(UUID requestId)
    {
        return (UpdateOutboxRequest)super.setRequestId(requestId);
    }

    @Override
    public UpdateOutboxRequest
    setTimestamp(Instant timestamp)
    {
        return (UpdateOutboxRequest)super.setTimestamp(timestamp);
    }

    public UpdateOutboxRequest
    setOutbox(UpdateOutboxData  entity)
    {
        outbox = entity;
        return this;
    }

    public UpdateOutboxData
    getOutbox() { return outbox; }
}

//////////////////////////////////////////////////////////////////////////////
