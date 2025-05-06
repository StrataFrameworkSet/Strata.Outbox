//////////////////////////////////////////////////////////////////////////////
// StartWorkerRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;

import java.time.Instant;
import java.util.UUID;

public
class StartWorkerRequest
    extends AbstractServiceRequest
{
    public
    StartWorkerRequest()
    {
        super();
    }

    @Override
    public StartWorkerRequest
    setRequestId(UUID requestId)
    {
        return (StartWorkerRequest)super.setRequestId(requestId);
    }

    @Override
    public StartWorkerRequest
    setTimestamp(Instant timestamp)
    {
        return (StartWorkerRequest)super.setTimestamp(timestamp);
    }

}

//////////////////////////////////////////////////////////////////////////////
