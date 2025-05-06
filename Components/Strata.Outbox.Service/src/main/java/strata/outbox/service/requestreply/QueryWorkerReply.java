//////////////////////////////////////////////////////////////////////////////
// QueryWorkerReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class QueryWorkerReply
    extends AbstractServiceReply
{
    private Boolean working;

    public
    QueryWorkerReply()
    {
        super();
        working = Boolean.FALSE;
    }

    public
    QueryWorkerReply(QueryWorkerRequest originatingRequest)
    {
        super(originatingRequest);
        working = Boolean.FALSE;
    }

    @Override
    public QueryWorkerReply
    setReplyId(UUID replyId)
    {
        return (QueryWorkerReply)super.setReplyId(replyId);
    }

    @Override
    public QueryWorkerReply
    setOriginatingRequestId(UUID requestId)
    {
        return (QueryWorkerReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public QueryWorkerReply
    setTimestamp(Instant timestamp)
    {
        return (QueryWorkerReply)super.setTimestamp(timestamp);
    }

    @Override
    public QueryWorkerReply
    setSuccess(boolean success)
    {
        return (QueryWorkerReply)super.setSuccess(success);
    }

    @Override
    public QueryWorkerReply
    setSuccessMessage(String successMessage)
    {
        return (QueryWorkerReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public QueryWorkerReply
    setFailureMessage(String failureMessage)
    {
        return (QueryWorkerReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public QueryWorkerReply
    setException(ExceptionData exception)
    {
        return (QueryWorkerReply)super.setException(exception);
    }

    public QueryWorkerReply
    setWorking(Boolean working)
    {
        this.working = working;
        return this;
    }

    public Boolean
    getWorking() { return working; }
}

//////////////////////////////////////////////////////////////////////////////
