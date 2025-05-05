import {AbstractServiceRequest} from "strata.foundation.core/Transfer/";

export
interface FindOutboxRequest
    extends AbstractServiceRequest
{
    outboxId: number;
}