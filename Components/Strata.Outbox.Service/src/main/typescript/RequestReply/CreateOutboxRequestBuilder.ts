import {AbstractServiceRequestBuilder} from "strata.foundation.core/Transfer";
import {CreateOutboxRequest} from "./CreateOutboxRequest";
import {CreateOutboxData} from "./CreateOutboxData";

export
class CreateOutboxRequestBuilder
    extends AbstractServiceRequestBuilder<CreateOutboxRequest>
{
    private outbox: CreateOutboxData;

    constructor()
    {
        super();
        this.outbox = null;
    }

    setOutbox(outbox:CreateOutboxData): CreateOutboxRequestBuilder
    {
        this.outbox = outbox;
        return this;
    }

    getOutbox(): CreateOutboxData
    {
        return this.outbox;
    }

    build(): CreateOutboxRequest
    {
        const request: CreateOutboxRequest =
            {
                requestId: this.getRequestId(),
                timestamp: this.getTimestamp().toString(),
                outbox: this.getOutbox()
            };

        return request;
    }
}