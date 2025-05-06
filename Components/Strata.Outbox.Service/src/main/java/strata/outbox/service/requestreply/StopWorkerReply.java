//////////////////////////////////////////////////////////////////////////////
// StopWorkerReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class StopWorkerReply
    extends AbstractServiceReply
{
    public
    StopWorkerReply()
    {
        super();
    }

    public
    StopWorkerReply(StopWorkerRequest originatingRequest)
    {
        super(originatingRequest);
    }

    @Override
    public StopWorkerReply
    setReplyId(UUID replyId)
    {
        return (StopWorkerReply)super.setReplyId(replyId);
    }

    @Override
    public StopWorkerReply
    setOriginatingRequestId(UUID requestId)
    {
        return (StopWorkerReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public StopWorkerReply
    setTimestamp(Instant timestamp)
    {
        return (StopWorkerReply)super.setTimestamp(timestamp);
    }

    @Override
    public StopWorkerReply
    setSuccess(boolean success)
    {
        return (StopWorkerReply)super.setSuccess(success);
    }

    @Override
    public StopWorkerReply
    setSuccessMessage(String successMessage)
    {
        return (StopWorkerReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public StopWorkerReply
    setFailureMessage(String failureMessage)
    {
        return (StopWorkerReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public StopWorkerReply
    setException(ExceptionData exception)
    {
        return (StopWorkerReply)super.setException(exception);
    }
}

//////////////////////////////////////////////////////////////////////////////
