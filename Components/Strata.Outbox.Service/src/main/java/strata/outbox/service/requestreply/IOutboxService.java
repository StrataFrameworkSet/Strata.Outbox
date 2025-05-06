//////////////////////////////////////////////////////////////////////////////
// IOutboxService.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import java.util.concurrent.CompletionStage;

public
interface IOutboxService
{
    CompletionStage<StartWorkerReply>
    startWorker(StartWorkerRequest request);

    CompletionStage<StopWorkerReply>
    stopWorker(StopWorkerRequest request);

    CompletionStage<QueryWorkerReply>
    queryWorker(QueryWorkerRequest request);

}

//////////////////////////////////////////////////////////////////////////////