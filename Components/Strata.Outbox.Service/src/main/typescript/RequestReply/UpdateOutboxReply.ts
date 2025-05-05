import {AbstractServiceReply} from "strata.foundation.core/Transfer";
import {OutboxData} from "../Unused";

export
interface UpdateOutboxReply
    extends AbstractServiceReply
{
    updatedOutbox: OutboxData;
}