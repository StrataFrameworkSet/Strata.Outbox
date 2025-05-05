import {AbstractServiceReply} from "strata.foundation.core/Transfer";
import {OutboxData} from "../Unused";

export
interface FindOutboxReply
    extends AbstractServiceReply
{
    foundOutbox: OutboxData;
}