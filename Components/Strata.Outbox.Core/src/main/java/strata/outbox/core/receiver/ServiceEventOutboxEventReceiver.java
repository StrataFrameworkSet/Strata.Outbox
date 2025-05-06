/// ///////////////////////////////////////////////////////////////////////////
// ServiceEventOutboxEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.foundation.core.event.IEventSender;
import strata.foundation.core.event.SendResult;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;

public
class ServiceEventOutboxEventReceiver<E,S extends IEventSender<E>>
    extends AbstractOutboxEventReceiver<E>
{
    private final Class<E> eventType;
    private final S        sender;
    private final Logger   logger;

    public
    ServiceEventOutboxEventReceiver(
        Class<E> eventType,
        S        sender)
    {
        super();
        this.eventType = eventType;
        this.sender = sender;
        this.logger = LogManager.getLogger(ServiceEventOutboxEventReceiver.class);
    }

    @Override
    protected E
    mapPayload(OutboxEvent event)
        throws MappingException
    {
        try
        {
            return
                getMapper()
                    .readValue(event.getEventPayload(),eventType);
        }
        catch (JsonProcessingException e)
        {
            throw
                new MappingException(
                    "Failed to map event payload to event type",e);
        }
    }

    @Override
    protected void
    processPayload(E payload)
        throws ReceiveException
    {
        sender
            .send(payload)
            .whenComplete((result,exception) -> processResult(result,exception));
    }

    protected void
    processResult(SendResult<E> result,Throwable exception)
    {
        if (result != null)
        {
            if (result.isSuccess())
                logger.info("Event sent successfully");
            else
                logger.error(
                    "Failed to send event",
                    result
                        .getException()
                        .orElse(new Exception("Unknown error")));
        }
        else if (exception != null)
            logger.error(
                "Failed to send event",
                exception);
        else
            logger.error(
                "Failed to send event",
                new Exception("Unknown error"));
    }
}

//////////////////////////////////////////////////////////////////////////////
