//////////////////////////////////////////////////////////////////////////////
// FindOutboxContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.FindOutboxRequest;

import java.util.Optional;

public
class FindOutboxContext
{
    private FindOutboxRequest request;
    private Optional<Outbox>  foundOutbox;

    public
    FindOutboxContext(FindOutboxRequest req)
    {
        request = req;
        foundOutbox = Optional.empty();
    }

    public FindOutboxContext
    setFoundOutbox(Optional<Outbox> updated)
    {
        foundOutbox = updated;
        return this;
    }

    public FindOutboxRequest
    getRequest() { return request; }

    public Long
    getOutboxId() { return request.getOutboxId(); }

    public Optional<Outbox>
    getFoundOutbox() { return foundOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
