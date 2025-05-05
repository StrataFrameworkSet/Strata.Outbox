import {AbstractServiceRequestBuilder} from "strata.foundation.core/Transfer";
import {DestroyOutboxRequest} from "./DestroyOutboxRequest";

export
class DestroyOutboxRequestBuilder
    extends AbstractServiceRequestBuilder<DestroyOutboxRequest>
{
    private outboxId: number;

    constructor()
    {
        super();
        this.outboxId = null;
    }

    setOutboxId(id: number): DestroyOutboxRequestBuilder
    {
        this.outboxId = id;
        return this;
    }

    getOutboxId(): number
    {
        return this.outboxId;
    }

    build(): DestroyOutboxRequest
    {
        const request: DestroyOutboxRequest =
            {
                requestId: this.getRequestId(),
                timestamp: this.getTimestamp().toString(),
                outboxId: this.getOutboxId()
            };

        return request;
    }
}