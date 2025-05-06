//////////////////////////////////////////////////////////////////////////////
// StartWorkerContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.StartWorkerRequest;

public
class StartWorkerContext
{
    private StartWorkerRequest request;
    private Boolean            working;

    public
    StartWorkerContext(StartWorkerRequest req)
    {
        request = req;
        working = Boolean.FALSE;
    }

    public StartWorkerContext
    setWorking(Boolean working)
    {
        this.working = working;
        return this;
    }

    public StartWorkerRequest
    getRequest() { return request; }

    public Boolean
    isWorking() { return working; }

}

//////////////////////////////////////////////////////////////////////////////
