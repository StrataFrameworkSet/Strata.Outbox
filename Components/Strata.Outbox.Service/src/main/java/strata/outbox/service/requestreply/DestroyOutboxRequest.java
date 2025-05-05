//////////////////////////////////////////////////////////////////////////////
// DestroyOutboxRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;

import java.time.Instant;
import java.util.UUID;

public
class DestroyOutboxRequest
    extends AbstractServiceRequest
{
    private Long   outboxId;

    public
    DestroyOutboxRequest()
    {
        super();
        outboxId = null;
    }

    @Override
    public DestroyOutboxRequest
    setRequestId(UUID requestId)
    {
        return (DestroyOutboxRequest)super.setRequestId(requestId);
    }

    @Override
    public DestroyOutboxRequest
    setTimestamp(Instant timestamp)
    {
        return (DestroyOutboxRequest)super.setTimestamp(timestamp);
    }

    public DestroyOutboxRequest
    setOutboxId(Long id)
    {
        outboxId = id;
        return this;
    }

    public Long
    getOutboxId() { return outboxId; }
}

//////////////////////////////////////////////////////////////////////////////
