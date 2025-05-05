//////////////////////////////////////////////////////////////////////////////
// OutboxKafkaEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.client.event;

import strata.outbox.service.event.IOutboxEventListener;
import strata.outbox.service.event.IOutboxEventReceiver;
import strata.outbox.service.event.OutboxEvent;
import strata.foundation.kafka.event.AbstractKafkaEventReceiver;

import java.util.Map;

public
class OutboxKafkaEventReceiver
    extends AbstractKafkaEventReceiver<OutboxEvent,IOutboxEventListener>
    implements IOutboxEventReceiver
{
    public
    OutboxKafkaEventReceiver(Map<String,Object> p,String topic)
    {
        super(p,OutboxEvent.class,topic);
    }
}

//////////////////////////////////////////////////////////////////////////////
