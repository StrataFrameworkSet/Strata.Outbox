//////////////////////////////////////////////////////////////////////////////
// StopWorkerRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;

import java.time.Instant;
import java.util.UUID;

public
class StopWorkerRequest
    extends AbstractServiceRequest
{
    public
    StopWorkerRequest()
    {
        super();
    }

    @Override
    public StopWorkerRequest
    setRequestId(UUID requestId)
    {
        return (StopWorkerRequest)super.setRequestId(requestId);
    }

    @Override
    public StopWorkerRequest
    setTimestamp(Instant timestamp)
    {
        return (StopWorkerRequest)super.setTimestamp(timestamp);
    }
}

//////////////////////////////////////////////////////////////////////////////
