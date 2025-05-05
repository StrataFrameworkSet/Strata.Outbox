import {AbstractServiceRequest} from "strata.foundation.core/Transfer/";

export
interface DestroyOutboxRequest
    extends AbstractServiceRequest
{
    outboxId: number;
}