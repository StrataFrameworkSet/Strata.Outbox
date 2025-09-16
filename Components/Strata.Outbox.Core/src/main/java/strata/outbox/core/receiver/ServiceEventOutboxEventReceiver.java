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
import strata.outbox.core.sender.SendException;
import strata.outbox.core.shared.MappingException;

import static strata.foundation.core.concurrent.Awaiter.await;

public abstract
class ServiceEventOutboxEventReceiver<E,S extends IEventSender<E>>
    extends AbstractOutboxEventReceiver<E>
{
    private final Class<E> eventType;
    private final S        sender;
    private final Logger   logger;

    protected
    ServiceEventOutboxEventReceiver(
        Class<E> eventType,
        S sender)
        throws SendException
    {
        super();
        this.eventType = eventType;
        this.sender = sender;
        this.logger = LogManager.getLogger(ServiceEventOutboxEventReceiver.class);
    }

    @Override
    public void
    open()
        throws ReceiveException
    {
        if (sender.isOpen())
        {
            logger.info("Event receiver is already open");
            return;
        }

        try
        {
            logger.info("Opening event receiver");
            sender.open();
        }
        catch (Exception e)
        {
            logger.error("Failed to open event receiver",e);
            throw new ReceiveException("Failed to open event receiver",e);
        }
    }

    @Override
    public void
    close() throws ReceiveException
    {
        if (sender.isClosed())
        {
            logger.info("Event receiver is already closed");
            return;
        }
        try
        {
            logger.info("Closing event receiver");
            sender.close();
        }
        catch (Exception e)
        {
            logger.error("Failed to close event receiver",e);
            throw new ReceiveException("Failed to close event receiver",e);
        }
    }

    @Override
    public boolean
    isOpen()
    {
        return sender.isOpen();
    }

    @Override
    public boolean
    isClosed()
    {
        return sender.isClosed();
    }

    @Override
    protected E
    mapPayload(OutboxEvent event)
        throws MappingException
    {
        try
        {
            logger.info(
                "mapping event payload {}",
                event.getEventPayload());
            return
                getMapper()
                    .readValue(event.getEventPayload(),eventType);
        }
        catch (JsonProcessingException e)
        {
            logger.error("Failed to map event payload",e);
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
        logger.info("Sending event {}", payload);

        try
        {
            SendResult<E> result = await(sender.send(payload));

            if (result.isSuccess())
                logger.info("Event sent successfully");
            else
                throw
                    result
                        .getException()
                        .orElseThrow(
                            () ->
                                new RuntimeException(
                                    "Failed to send event, but no exception was provided"));
        }
        catch (Throwable e)
        {
            logger.error("Failed to send event",e);
            throw new ReceiveException(e);
        }
    }

    protected void
    openSenderIfNeeded() throws SendException
    {
        if (sender.isClosed())
        {
            try
            {
                logger.info("Opening event sender");
                sender.open();
            }
            catch (Exception e)
            {
                logger.error(
                    "Failed to open event sender",
                    e);
                throw new SendException(e);
            }
        }
    }

    protected void
    closeSenderIfNeeded() throws SendException
    {
        if (sender.isOpen())
        {
            try
            {
                logger.info("Closing event sender");
                sender.close();
            }
            catch (Exception e)
            {
                logger.error(
                    "Failed to close event sender",
                    e);
                throw new SendException(e);
            }
        }
    }

}

//////////////////////////////////////////////////////////////////////////////
