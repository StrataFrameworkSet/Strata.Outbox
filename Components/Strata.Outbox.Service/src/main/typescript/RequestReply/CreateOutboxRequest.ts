import {AbstractServiceRequest} from "strata.foundation.core/Transfer/";
import {CreateOutboxData} from "./CreateOutboxData";

export
interface CreateOutboxRequest
    extends AbstractServiceRequest
{
    outbox: CreateOutboxData;
}