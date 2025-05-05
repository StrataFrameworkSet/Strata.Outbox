//////////////////////////////////////////////////////////////////////////////
// CreateOutboxReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class CreateOutboxReply
    extends AbstractServiceReply
{
    private OutboxData createdOutbox;

    public
    CreateOutboxReply()
    {
        super();
        createdOutbox = null;
    }

    public
    CreateOutboxReply(CreateOutboxRequest originatingRequest)
    {
        super(originatingRequest);
        createdOutbox = null;
    }

    @Override
    public CreateOutboxReply
    setReplyId(UUID replyId)
    {
        return (CreateOutboxReply)super.setReplyId(replyId);
    }

    @Override
    public CreateOutboxReply
    setOriginatingRequestId(UUID requestId)
    {
        return (CreateOutboxReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public CreateOutboxReply
    setTimestamp(Instant timestamp)
    {
        return (CreateOutboxReply)super.setTimestamp(timestamp);
    }

    @Override
    public CreateOutboxReply
    setSuccess(boolean success)
    {
        return (CreateOutboxReply)super.setSuccess(success);
    }

    @Override
    public CreateOutboxReply
    setSuccessMessage(String successMessage)
    {
        return (CreateOutboxReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public CreateOutboxReply
    setFailureMessage(String failureMessage)
    {
        return (CreateOutboxReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public CreateOutboxReply
    setException(ExceptionData exception)
    {
        return (CreateOutboxReply)super.setException(exception);
    }

    public CreateOutboxReply
    setCreatedOutbox(OutboxData created)
    {
        createdOutbox = created;
        return this;
    }

    public OutboxData
    getCreatedOutbox() { return createdOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
