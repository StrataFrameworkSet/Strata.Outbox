import {IOutboxService} from "strata.outbox.service/RequestReply";

export
interface IOutboxServiceClient
    extends IOutboxService
{
    setAuthorization(accessToken: string): IOutboxServiceClient;

    close(): void;
}