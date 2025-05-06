//////////////////////////////////////////////////////////////////////////////
// CreateOutboxContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.CreateOutboxData;
import strata.outbox.service.requestreply.CreateOutboxRequest;

import java.util.Optional;

public
class CreateOutboxContext
{
    private CreateOutboxRequest request;
    private Optional<Outbox>    createdOutbox;

    public
    CreateOutboxContext(CreateOutboxRequest req)
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

    public CreateOutboxRequest
    getRequest() { return request; }

    public CreateOutboxData
    getOutboxToCreate() { return request.getOutbox(); }

    public Optional<Outbox>
    getCreatedOutbox() { return createdOutbox; }
}

//////////////////////////////////////////////////////////////////////////////
