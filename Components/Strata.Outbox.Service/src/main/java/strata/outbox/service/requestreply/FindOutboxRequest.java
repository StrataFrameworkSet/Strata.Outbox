//////////////////////////////////////////////////////////////////////////////
// DestroyOutboxRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;

import java.time.Instant;
import java.util.UUID;

public
class FindOutboxRequest
    extends AbstractServiceRequest
{
    private Long   outboxId;

    public FindOutboxRequest()
    {
        super();
        outboxId = null;
    }

    @Override
    public FindOutboxRequest
    setRequestId(UUID requestId)
    {
        return (FindOutboxRequest)super.setRequestId(requestId);
    }

    @Override
    public FindOutboxRequest
    setTimestamp(Instant timestamp)
    {
        return (FindOutboxRequest)super.setTimestamp(timestamp);
    }

    public FindOutboxRequest
    setOutboxId(Long id)
    {
        outboxId = id;
        return this;
    }

    public Long
    getOutboxId() { return outboxId; }
}

//////////////////////////////////////////////////////////////////////////////
