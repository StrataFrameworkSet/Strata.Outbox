//////////////////////////////////////////////////////////////////////////////
// IOutboxService.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import java.util.concurrent.CompletionStage;

public
interface IOutboxService
{
    CompletionStage<CreateOutboxReply>
    createOutbox(CreateOutboxRequest request);

    CompletionStage<UpdateOutboxReply>
    updateOutbox(UpdateOutboxRequest request);

    CompletionStage<DestroyOutboxReply>
    destroyOutbox(DestroyOutboxRequest request);

    CompletionStage<FindOutboxReply>
    findOutbox(FindOutboxRequest request);
}

//////////////////////////////////////////////////////////////////////////////