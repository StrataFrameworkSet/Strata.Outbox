//////////////////////////////////////////////////////////////////////////////
// DestroyOutboxReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class FindOutboxReply
    extends AbstractServiceReply
{
    private OutboxData foundOutbox;

    public
    FindOutboxReply()
    {
        super();
        foundOutbox = null;
    }

    public
    FindOutboxReply(FindOutboxRequest originatingRequest)
    {
        super(originatingRequest);
        foundOutbox = null;
    }

    @Override
    public FindOutboxReply
    setReplyId(UUID replyId)
    {
        return (FindOutboxReply)super.setReplyId(replyId);
    }

    @Override
    public FindOutboxReply
    setOriginatingRequestId(UUID requestId)
    {
        return (FindOutboxReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public FindOutboxReply
    setTimestamp(Instant timestamp)
    {
        return (FindOutboxReply)super.setTimestamp(timestamp);
    }

    @Override
    public FindOutboxReply
    setSuccess(boolean success)
    {
        return (FindOutboxReply)super.setSuccess(success);
    }

    @Override
    public FindOutboxReply
    setSuccessMessage(String successMessage)
    {
        return (FindOutboxReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public FindOutboxReply
    setFailureMessage(String failureMessage)
    {
        return (FindOutboxReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public FindOutboxReply
    setException(ExceptionData exception)
    {
        return (FindOutboxReply)super.setException(exception);
    }

    public FindOutboxReply
    setFoundOutbox(OutboxData created)
    {
        foundOutbox = created;
        return this;
    }

    public OutboxData
    getFoundOutbox() { return foundOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
