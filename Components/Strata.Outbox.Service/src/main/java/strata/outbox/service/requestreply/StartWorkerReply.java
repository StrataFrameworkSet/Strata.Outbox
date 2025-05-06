//////////////////////////////////////////////////////////////////////////////
// StartWorkerReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class StartWorkerReply
    extends AbstractServiceReply
{
    public
    StartWorkerReply()
    {
        super();
    }

    public
    StartWorkerReply(StartWorkerRequest originatingRequest)
    {
        super(originatingRequest);
    }

    @Override
    public StartWorkerReply
    setReplyId(UUID replyId)
    {
        return (StartWorkerReply)super.setReplyId(replyId);
    }

    @Override
    public StartWorkerReply
    setOriginatingRequestId(UUID requestId)
    {
        return (StartWorkerReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public StartWorkerReply
    setTimestamp(Instant timestamp)
    {
        return (StartWorkerReply)super.setTimestamp(timestamp);
    }

    @Override
    public StartWorkerReply
    setSuccess(boolean success)
    {
        return (StartWorkerReply)super.setSuccess(success);
    }

    @Override
    public StartWorkerReply
    setSuccessMessage(String successMessage)
    {
        return (StartWorkerReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public StartWorkerReply
    setFailureMessage(String failureMessage)
    {
        return (StartWorkerReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public StartWorkerReply
    setException(ExceptionData exception)
    {
        return (StartWorkerReply)super.setException(exception);
    }
}

//////////////////////////////////////////////////////////////////////////////
