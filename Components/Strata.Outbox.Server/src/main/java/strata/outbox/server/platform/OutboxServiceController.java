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
        value = "/start-worker",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<StartWorkerReply>
    startWorker(@RequestBody StartWorkerRequest request)
    {
        logger.info("delegating start-worker request to implementation service");
        return implementation.startWorker(request);
    }

    @PostMapping(
        value = "/stop-worker",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<StopWorkerReply>
    stopWorker(@RequestBody StopWorkerRequest request)
    {
        logger.info("delegating stop-worker request to implementation service");
        return implementation.stopWorker(request);
    }

    @PostMapping(
        value = "/query-worker",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<QueryWorkerReply>
    queryWorker(@RequestBody QueryWorkerRequest request)
    {
        logger.info("delegating query-worker to implementation service");
        return implementation.queryWorker(request);
    }

}

//////////////////////////////////////////////////////////////////////////////
