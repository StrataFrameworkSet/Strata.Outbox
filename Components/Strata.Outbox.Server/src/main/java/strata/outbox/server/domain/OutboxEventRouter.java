/// ///////////////////////////////////////////////////////////////////////////
// OutboxEventRouter.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.outbox.core.receiver.IOutboxEventReceiver;
import strata.outbox.core.repository.OutboxEvent;

import java.util.Map;
import java.util.Optional;

public
class OutboxEventRouter
    implements IOutboxEventRouter
{
    private final Map<String,IOutboxEventReceiver> receivers;
    private final Logger                           logger;

    public
    OutboxEventRouter(Map<String,IOutboxEventReceiver> receivers)
    {
        this.receivers = receivers;
        this.logger = LogManager.getLogger(OutboxEventRouter.class);
    }

    @Override
    public void
    route(OutboxEvent event)
    {
        logger.info("Routing outbox event ({})", event.getId());
        getReceiver(event)
            .ifPresentOrElse(
                receiver -> receiver.receive(event),
                () ->
                    {
                        logger.error(
                            "No receiver found for event type: {}",
                            event.getEventType());
                        throw new IllegalArgumentException(
                            "No receiver found for event type: " + event.getEventType());
                    });
    }

    protected Optional<IOutboxEventReceiver>
    getReceiver(OutboxEvent event)
    {
        return Optional.ofNullable(receivers.get(event.getEventType()));
    }
}

//////////////////////////////////////////////////////////////////////////////
