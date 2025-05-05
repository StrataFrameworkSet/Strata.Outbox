//////////////////////////////////////////////////////////////////////////////
// CreateOutboxRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;

import java.time.Instant;
import java.util.UUID;

public
class CreateOutboxRequest
    extends AbstractServiceRequest
{
    private CreateOutboxData outbox;

    public
    CreateOutboxRequest()
    {
        super();
        outbox = null;
    }

    @Override
    public CreateOutboxRequest
    setRequestId(UUID requestId)
    {
        return (CreateOutboxRequest)super.setRequestId(requestId);
    }

    @Override
    public CreateOutboxRequest
    setTimestamp(Instant timestamp)
    {
        return (CreateOutboxRequest)super.setTimestamp(timestamp);
    }

    public CreateOutboxRequest
    setOutbox(CreateOutboxData entity)
    {
        outbox = entity;
        return this;
    }

    public CreateOutboxData
    getOutbox() { return outbox; }
}

//////////////////////////////////////////////////////////////////////////////
