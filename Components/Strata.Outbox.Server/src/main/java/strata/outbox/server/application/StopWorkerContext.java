//////////////////////////////////////////////////////////////////////////////
// StopWorkerContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.StopWorkerRequest;

public
class StopWorkerContext
{
    private StopWorkerRequest request;
    private Boolean           working;

    public
    StopWorkerContext(StopWorkerRequest req)
    {
        request = req;
        working = Boolean.FALSE;
    }

    public StopWorkerContext
    setWorking(Boolean working)
    {
        this.working = working;
        return this;
    }

    public StopWorkerRequest
    getRequest() { return request; }

    public Boolean
    isWorking() { return working; }

}

//////////////////////////////////////////////////////////////////////////////
