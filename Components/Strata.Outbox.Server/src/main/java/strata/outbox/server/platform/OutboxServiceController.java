//////////////////////////////////////////////////////////////////////////////
// OutboxServiceController.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import strata.outbox.service.requestreply.*;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionStage;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/outbox-service")
@EnableTransactionManagement
public
class OutboxServiceController
{
    private IOutboxService implementation;
    private Logger          logger;

    @Inject
    public
    OutboxServiceController(IOutboxService imp)
    {
        implementation = imp;
        logger = LogManager.getLogger(OutboxServiceController.class);
    }

    @PostMapping(
        value = "/create-outbox",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<StartWorkerReply>
    createOutbox(@RequestBody StartWorkerRequest request)
    {
        logger.info("delegating createOutbox to implementation service");
        return implementation.startWorker(request);
    }

    @PostMapping(
        value = "/update-outbox",
        produces = MediaType.APPLICATION_JSON_VALUE)
    //@Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public @ResponseBody CompletionStage<StopWorkerReply>
    updateOutbox(@RequestBody StopWorkerRequest request)
    {
        logger.info("delegating updateOutbox to implementation service");
        return implementation.stopWorker(request);
    }

    @PostMapping(
        value = "/destroy-outbox",
        produces = MediaType.APPLICATION_JSON_VALUE)
    //@Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public @ResponseBody CompletionStage<QueryWorkerReply>
    destroyOutbox(@RequestBody QueryWorkerRequest request)
    {
        logger.info("delegating updateOutbox to implementation service");
        return implementation.queryWorker(request);
    }

    @PostMapping(
        value = "/find-outbox",
        produces = MediaType.APPLICATION_JSON_VALUE)
    //@Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public @ResponseBody CompletionStage<FindOutboxReply>
    findOutbox(@RequestBody FindOutboxRequest request)
    {
        logger.info("delegating findSession to implementation service");
        return implementation.findOutbox(request);
    }
}

//////////////////////////////////////////////////////////////////////////////
