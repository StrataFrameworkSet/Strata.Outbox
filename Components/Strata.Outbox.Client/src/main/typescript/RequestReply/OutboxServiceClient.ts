import {IOutboxServiceClient} from "./IOutboxServiceClient";
import {ICompletionStage} from "strata.foundation.core/Concurrent";
import {AbstractRestClient} from "strata.client.core/Service";
import {Guid} from "guid-typescript";
import {AbstractServiceRequest} from "strata.foundation.core/Transfer";
import {Instant} from "@js-joda/core";
import {
    CreateOutboxReply,
    CreateOutboxRequest,DestroyOutboxReply,
    DestroyOutboxRequest,FindOutboxReply,FindOutboxRequest,
    UpdateOutboxReply,
    UpdateOutboxRequest
} from "strata.outbox.service/RequestReply";

export
class OutboxServiceClient
    extends AbstractRestClient
    implements IOutboxServiceClient
{
    constructor(baseUrl: string)
    {
        super(baseUrl);
        this.setHeader("Content-Type","application/json");
        this.setHeader("Access-Control-Allow-Origin","*");
    }

    setAuthorization(accessToken: string): IOutboxServiceClient
    {
        if (super.hasHeader("Authorization"))
            super.clearHeader("Authorization");

        super.setHeader("Authorization","Bearer " + accessToken);
        return this;
    }

    close(): void
    {
        super.clearHeaders();
    }

    createOutbox(request: CreateOutboxRequest): ICompletionStage<CreateOutboxReply>
    {
        console.log("OutboxServiceClient.createOutbox");
        return this.doPost<CreateOutboxReply,CreateOutboxRequest>(
            "create-outbox",
            this.initializeRequest(request));
    }

    updateOutbox(request: UpdateOutboxRequest): ICompletionStage<UpdateOutboxReply>
    {
        console.log("OutboxServiceClient.updateOutbox");
        return this.doPost<UpdateOutboxReply,UpdateOutboxRequest>(
            "update-outbox",
            this.initializeRequest(request));
    }

    destroyOutbox(request: DestroyOutboxRequest): ICompletionStage<DestroyOutboxReply>
    {
        console.log("OutboxServiceClient.destroyOutbox");
        return this.doPost<DestroyOutboxReply,DestroyOutboxRequest>(
            "destroy-outbox",
            this.initializeRequest(request));
    }

    findOutbox(request: FindOutboxRequest): ICompletionStage<FindOutboxReply>
    {
        console.log("OutboxServiceClient.find-outbox");
        return this.doPost<FindOutboxReply,FindOutboxRequest>(
            "find-outbox",
            this.initializeRequest(request));
    }

    private  initializeRequest<R extends AbstractServiceRequest>(request: R): R
    {
        console.log("OutboxServiceClient.initializeRequest");
        if (request == null)
            request.requestId = Guid.create().toString();

        if (request.timestamp == null)
            request.timestamp = Instant.now().toString();

        return request;
    }

}