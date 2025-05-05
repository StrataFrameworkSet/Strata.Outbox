//////////////////////////////////////////////////////////////////////////////
// OutboxKafkaAvroEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.client.event;

import strata.outbox.service.event.IOutboxEventListener;
import strata.outbox.service.event.IOutboxEventReceiver;
import strata.outbox.service.event.OutboxEvent;
import strata.foundation.kafka.event.AbstractKafkaAvroEventReceiver;

import java.util.Map;

public
class OutboxKafkaAvroEventReceiver
    extends AbstractKafkaAvroEventReceiver<OutboxEvent,IOutboxEventListener>
    implements IOutboxEventReceiver
{
    public
    OutboxKafkaAvroEventReceiver(Map<String,Object> properties,String topic)
    {
        super(properties,OutboxEvent.class,topic);
    }
}

//////////////////////////////////////////////////////////////////////////////
