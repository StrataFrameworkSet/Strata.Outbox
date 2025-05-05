import {AbstractServiceRequestBuilder} from "strata.foundation.core/Transfer";
import {FindOutboxRequest} from "./FindOutboxRequest";

export
class FindOutboxRequestBuilder
    extends AbstractServiceRequestBuilder<FindOutboxRequest>
{
    private outboxId: number;

    constructor()
    {
        super();
        this.outboxId = null;
    }

    setOutboxId(id: number): FindOutboxRequestBuilder
    {
        this.outboxId = id;
        return this;
    }

    getOutboxId(): number
    {
        return this.outboxId;
    }

    build(): FindOutboxRequest
    {
        const request: FindOutboxRequest =
            {
                requestId: this.getRequestId(),
                timestamp: this.getTimestamp().toString(),
                outboxId: this.getOutboxId()
            };

        return request;
    }
}