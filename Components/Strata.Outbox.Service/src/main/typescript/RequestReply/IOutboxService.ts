import {ICompletionStage} from "strata.foundation.core/Concurrent";
import {CreateOutboxRequest} from "./CreateOutboxRequest";
import {CreateOutboxReply} from "./CreateOutboxReply";
import {UpdateOutboxRequest} from "./UpdateOutboxRequest";
import {UpdateOutboxReply} from "./UpdateOutboxReply";
import {DestroyOutboxRequest} from "./DestroyOutboxRequest";
import {DestroyOutboxReply} from "./DestroyOutboxReply";
import {FindOutboxRequest} from "./FindOutboxRequest";
import {FindOutboxReply} from "./FindOutboxReply";

export
interface IOutboxService
{
    createOutbox(request: CreateOutboxRequest):
        ICompletionStage<CreateOutboxReply>;

    updateOutbox(request: UpdateOutboxRequest):
        ICompletionStage<UpdateOutboxReply>;

    destroyOutbox(request: DestroyOutboxRequest):
        ICompletionStage<DestroyOutboxReply>;

    findOutbox(request: FindOutboxRequest):
        ICompletionStage<FindOutboxReply>;
}