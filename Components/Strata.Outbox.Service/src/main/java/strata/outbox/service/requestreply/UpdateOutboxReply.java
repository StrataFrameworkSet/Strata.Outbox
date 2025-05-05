//////////////////////////////////////////////////////////////////////////////
// UpdateOutboxReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class UpdateOutboxReply
    extends AbstractServiceReply
{
    private OutboxData updatedOutbox;

    public
    UpdateOutboxReply()
    {
        super();
        updatedOutbox = null;
    }

    public
    UpdateOutboxReply(UpdateOutboxRequest originatingRequest)
    {
        super(originatingRequest);
        updatedOutbox = null;
    }

    @Override
    public UpdateOutboxReply
    setReplyId(UUID replyId)
    {
        return (UpdateOutboxReply)super.setReplyId(replyId);
    }

    @Override
    public UpdateOutboxReply
    setOriginatingRequestId(UUID requestId)
    {
        return (UpdateOutboxReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public UpdateOutboxReply
    setTimestamp(Instant timestamp)
    {
        return (UpdateOutboxReply)super.setTimestamp(timestamp);
    }

    @Override
    public UpdateOutboxReply
    setSuccess(boolean success)
    {
        return (UpdateOutboxReply)super.setSuccess(success);
    }

    @Override
    public UpdateOutboxReply
    setSuccessMessage(String successMessage)
    {
        return (UpdateOutboxReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public UpdateOutboxReply
    setFailureMessage(String failureMessage)
    {
        return (UpdateOutboxReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public UpdateOutboxReply
    setException(ExceptionData exception)
    {
        return (UpdateOutboxReply)super.setException(exception);
    }

    public UpdateOutboxReply
    setUpdatedOutbox(OutboxData created)
    {
        updatedOutbox = created;
        return this;
    }

    public OutboxData
    getUpdatedOutbox() { return updatedOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
