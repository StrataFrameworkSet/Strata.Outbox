/// ///////////////////////////////////////////////////////////////////////////
// OutboxEventRouter.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import strata.outbox.core.receiver.IOutboxEventReceiver;
import strata.outbox.core.repository.OutboxEvent;

import java.util.Map;
import java.util.Optional;

public
class OutboxEventRouter
    implements IOutboxEventRouter
{
    private final Map<String,IOutboxEventReceiver> receivers;

    public
    OutboxEventRouter(Map<String,IOutboxEventReceiver> receivers)
    {
        this.receivers = receivers;
    }

    @Override
    public void
    route(OutboxEvent event)
    {
        getReceiver(event)
            .ifPresentOrElse(
                receiver -> receiver.receive(event),
                () ->
                    {
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
