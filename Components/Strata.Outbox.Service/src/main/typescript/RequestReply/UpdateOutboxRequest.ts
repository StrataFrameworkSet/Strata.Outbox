import {AbstractServiceRequest} from "strata.foundation.core/Transfer/";
import {UpdateOutboxData} from "./UpdateOutboxData";

export
interface UpdateOutboxRequest
    extends AbstractServiceRequest
{
    outbox: UpdateOutboxData;
}