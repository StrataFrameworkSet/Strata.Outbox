import {AbstractServiceReply} from "strata.foundation.core/Transfer";
import {OutboxData} from "../Unused";

export
interface DestroyOutboxReply
    extends AbstractServiceReply
{
    destroyedOutbox: OutboxData;
}