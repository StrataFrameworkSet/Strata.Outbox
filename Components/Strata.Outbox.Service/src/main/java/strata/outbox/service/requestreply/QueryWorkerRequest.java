//////////////////////////////////////////////////////////////////////////////
// QueryWorkerRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;

import java.time.Instant;
import java.util.UUID;

public
class QueryWorkerRequest
    extends AbstractServiceRequest
{
    private Long   outboxId;

    public QueryWorkerRequest()
    {
        super();
        outboxId = null;
    }

    @Override
    public QueryWorkerRequest
    setRequestId(UUID requestId)
    {
        return (QueryWorkerRequest)super.setRequestId(requestId);
    }

    @Override
    public QueryWorkerRequest
    setTimestamp(Instant timestamp)
    {
        return (QueryWorkerRequest)super.setTimestamp(timestamp);
    }

    public QueryWorkerRequest
    setOutboxId(Long id)
    {
        outboxId = id;
        return this;
    }

    public Long
    getOutboxId() { return outboxId; }
}

//////////////////////////////////////////////////////////////////////////////
