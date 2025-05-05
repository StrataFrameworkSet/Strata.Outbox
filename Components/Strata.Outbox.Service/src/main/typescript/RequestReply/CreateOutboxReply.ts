import {AbstractServiceReply} from "strata.foundation.core/Transfer";
import {OutboxData} from "../Unused";

export
interface CreateOutboxReply
    extends AbstractServiceReply
{
    created__Entity: OutboxData;
}