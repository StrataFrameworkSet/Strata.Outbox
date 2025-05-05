//////////////////////////////////////////////////////////////////////////////
// DestroyOutboxReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class DestroyOutboxReply
    extends AbstractServiceReply
{
    private OutboxData destroyedOutbox;

    public
    DestroyOutboxReply()
    {
        super();
        destroyedOutbox = null;
    }

    public
    DestroyOutboxReply(DestroyOutboxRequest originatingRequest)
    {
        super(originatingRequest);
        destroyedOutbox = null;
    }

    @Override
    public DestroyOutboxReply
    setReplyId(UUID replyId)
    {
        return (DestroyOutboxReply)super.setReplyId(replyId);
    }

    @Override
    public DestroyOutboxReply
    setOriginatingRequestId(UUID requestId)
    {
        return (DestroyOutboxReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public DestroyOutboxReply
    setTimestamp(Instant timestamp)
    {
        return (DestroyOutboxReply)super.setTimestamp(timestamp);
    }

    @Override
    public DestroyOutboxReply
    setSuccess(boolean success)
    {
        return (DestroyOutboxReply)super.setSuccess(success);
    }

    @Override
    public DestroyOutboxReply
    setSuccessMessage(String successMessage)
    {
        return (DestroyOutboxReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public DestroyOutboxReply
    setFailureMessage(String failureMessage)
    {
        return (DestroyOutboxReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public DestroyOutboxReply
    setException(ExceptionData exception)
    {
        return (DestroyOutboxReply)super.setException(exception);
    }

    public DestroyOutboxReply
    setDestroyedOutbox(OutboxData created)
    {
        destroyedOutbox = created;
        return this;
    }

    public OutboxData
    getDestroyedOutbox() { return destroyedOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
