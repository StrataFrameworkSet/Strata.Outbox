//////////////////////////////////////////////////////////////////////////////
// OutboxServiceController.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import strata.outbox.service.requestreply.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    public @ResponseBody CompletionStage<CreateOutboxReply>
    createOutbox(@RequestBody CreateOutboxRequest request)
    {
        logger.info("delegating createOutbox to implementation service");
        return implementation.createOutbox(request);
    }

    @PostMapping(
        value = "/update-outbox",
        produces = MediaType.APPLICATION_JSON_VALUE)
    //@Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public @ResponseBody CompletionStage<UpdateOutboxReply>
    updateOutbox(@RequestBody UpdateOutboxRequest request)
    {
        logger.info("delegating updateOutbox to implementation service");
        return implementation.updateOutbox(request);
    }

    @PostMapping(
        value = "/destroy-outbox",
        produces = MediaType.APPLICATION_JSON_VALUE)
    //@Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public @ResponseBody CompletionStage<DestroyOutboxReply>
    destroyOutbox(@RequestBody DestroyOutboxRequest request)
    {
        logger.info("delegating updateOutbox to implementation service");
        return implementation.destroyOutbox(request);
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
