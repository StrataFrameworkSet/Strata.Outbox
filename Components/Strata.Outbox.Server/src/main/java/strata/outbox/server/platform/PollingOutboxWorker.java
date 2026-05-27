//////////////////////////////////////////////////////////////////////////////
// PollingOutboxWorker.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.foundation.core.inject.IInjector;
import strata.foundation.core.inject.Operation;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.IOutboxEventProcessor;
import strata.outbox.server.domain.IOutboxEventRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public
class PollingOutboxWorker
    implements IOutboxWorker
{
    private final IInjector                     injector;
    private final IOutboxEventRouter             router;
    private volatile ScheduledExecutorService scheduler;
    private final AtomicBoolean                  working;
    private final Logger                         logger;
    private static final long                    POLL_INTERVAL_MS = 1000;

    @Inject
    public
    PollingOutboxWorker(IInjector injector,IOutboxEventRouter router)
    {
        this.injector = injector;
        this.router = router;
        this.scheduler = null;
        this.working = new AtomicBoolean(false);
        this.logger = LogManager.getLogger(getClass());
    }

    @Override
    public void
    start()
    {
        if (working.compareAndSet(false, true))
        {
            logger.info("Starting polling outbox worker");

            try
            {
                router.open();
            }
            catch (Exception e)
            {
                logger.error("Error opening router", e);
                working.set(false);
                return;
            }

            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleWithFixedDelay(
                this::poll,
                0,
                POLL_INTERVAL_MS,
                TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void
    stop()
    {
        if (working.compareAndSet(true, false))
        {
            logger.info("Stopping polling outbox worker");
            scheduler.shutdown();

            try
            {
                scheduler.awaitTermination(30, TimeUnit.SECONDS);
                router.close();
            }
            catch (Exception e)
            {
                logger.error("Error stopping worker", e);
            }
        }
    }

    @Override
    public boolean
    isWorking()
    {
        return working.get();
    }

    private void
    poll()
    {
        List<OutboxEvent> pending = getPending();
        List<OutboxEvent> working = getWorking(pending);

        processWorking(working);
    }

    private List<OutboxEvent>
    getPending()
    {
        try (Operation operation = new Operation(injector))
        {
            IOutboxEventProcessor processor =
                operation.getInstance(IOutboxEventProcessor.class);

            return processor.getPending();
        }
        catch (Exception e)
        {
            logger.error("Error polling outbox events", e);
            return new ArrayList<>();
        }
    }

    private List<OutboxEvent>
    getWorking(List<OutboxEvent> pending)
    {
        return
            pending
                .stream()
                .map(this::checkOut)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<OutboxEvent>
    checkOut(OutboxEvent event)
    {
        try (Operation operation = new Operation(injector))
        {
            IOutboxEventProcessor processor =
                operation.getInstance(IOutboxEventProcessor.class);

            return Optional.ofNullable(processor.checkOut(event));
        }
        catch (Exception e)
        {
            logger.error("Error checking out outbox event", e);
            return Optional.empty();
        }
    }

    private void
    processWorking(List<OutboxEvent> working)
    {
        working.forEach(this::processOne);
    }

    private void
    processOne(OutboxEvent event)
    {
        try (Operation operation = new Operation(injector))
        {
            IOutboxEventProcessor processor =
                operation.getInstance(IOutboxEventProcessor.class);

            processor.processEvent(event);
        }
        catch (Exception e)
        {
            logger.error("Error processing outbox event {}", event.getId(), e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
