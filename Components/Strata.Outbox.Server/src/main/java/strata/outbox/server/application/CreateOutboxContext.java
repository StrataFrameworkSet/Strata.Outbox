//////////////////////////////////////////////////////////////////////////////
// CreateOutboxContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.StartWorkerRequest;

import java.util.Optional;

public
class CreateOutboxContext
{
    private StartWorkerRequest request;
    private Optional<Outbox>    createdOutbox;

    public
    CreateOutboxContext(StartWorkerRequest req)
    {
        request = req;
        createdOutbox = Optional.empty();
    }

    public CreateOutboxContext
    setCreatedOutbox(Outbox created)
    {
        createdOutbox = Optional.of(created);
        return this;
    }

    public StartWorkerRequest
    getRequest() { return request; }

    public CreateOutboxData
    getOutboxToCreate() { return request.getOutbox(); }

    public Optional<Outbox>
    getCreatedOutbox() { return createdOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
