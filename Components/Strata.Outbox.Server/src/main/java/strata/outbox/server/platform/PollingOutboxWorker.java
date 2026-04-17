//////////////////////////////////////////////////////////////////////////////
// PollingOutboxWorker.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.IOutboxEventRouter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public
class PollingOutboxWorker
    implements IOutboxWorker
{
    private final IOutboxEventRepository   repository;
    private final IOutboxEventRouter       router;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean            working;
    private final Logger                   logger;
    private static final Integer           MAX_ATTEMPTS = 5;
    private static final long              POLL_INTERVAL_MS = 1000;

    @Inject
    public
    PollingOutboxWorker(
        IOutboxEventRouter     router,
        IOutboxEventRepository repository)
    {
        this.router = router;
        this.repository = repository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
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
        try
        {
            for (OutboxEvent event : repository.findAll())
            {
                processEvent(event);
            }
        }
        catch (Exception e)
        {
            logger.error("Error polling outbox events", e);
        }
    }

    private void
    processEvent(OutboxEvent event)
    {
        try
        {
            logger.info(
                "Attempt {} for outbox event {}",
                event.getAttempt(),
                event.getId());

            if (event.getAttempt() > MAX_ATTEMPTS)
            {
                logger.error(
                    "Aborting outbox event {} after exceeding max attempts {}",
                    event.getId(),
                    event.getAttempt());
                return;
            }

            router.route(event);
            logger.info("Deleting completed outbox event {}", event.getId());
            repository.delete(event);
        }
        catch (Exception ex)
        {
            logger.error("Error processing outbox event {}", event.getId(), ex);
            logger.info(
                "Incrementing attempt for outbox event {} and re-saving",
                event.getId());
            repository.save(event.incrementAttempt());
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
