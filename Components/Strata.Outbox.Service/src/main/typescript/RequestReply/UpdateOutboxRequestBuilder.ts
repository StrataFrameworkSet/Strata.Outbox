import {AbstractServiceRequestBuilder} from "strata.foundation.core/Transfer";
import {UpdateOutboxRequest} from "./UpdateOutboxRequest";
import {UpdateOutboxData} from "./UpdateOutboxData";

export
class UpdateOutboxRequestBuilder
    extends AbstractServiceRequestBuilder<UpdateOutboxRequest>
{
    private outbox: UpdateOutboxData;

    constructor()
    {
        super();
        this.outbox = null;
    }

    setOutbox(outbox: UpdateOutboxData): UpdateOutboxRequestBuilder
    {
        this.outbox = outbox;
        return this;
    }

    getOutbox(): UpdateOutboxData
    {
        return this.outbox;
    }

    build(): UpdateOutboxRequest
    {
        const request: UpdateOutboxRequest =
            {
                requestId: this.getRequestId(),
                timestamp: this.getTimestamp().toString(),
                outbox: this.getOutbox()
            };

        return request;
    }
}