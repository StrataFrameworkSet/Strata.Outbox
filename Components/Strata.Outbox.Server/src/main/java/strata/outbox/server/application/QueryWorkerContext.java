//////////////////////////////////////////////////////////////////////////////
// StopWorkerContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.QueryWorkerRequest;

public
class QueryWorkerContext
{
    private QueryWorkerRequest request;
    private Boolean            working;

    public
    QueryWorkerContext(QueryWorkerRequest req)
    {
        request = req;
        working = Boolean.FALSE;
    }

    public QueryWorkerContext
    setWorking(Boolean working)
    {
        this.working = working;
        return this;
    }

    public QueryWorkerRequest
    getRequest() { return request; }

    public Boolean
    isWorking() { return working; }

}

//////////////////////////////////////////////////////////////////////////////
