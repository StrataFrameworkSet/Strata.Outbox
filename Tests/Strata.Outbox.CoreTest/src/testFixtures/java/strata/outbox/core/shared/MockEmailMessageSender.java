/// ///////////////////////////////////////////////////////////////////////////
// MockEmailMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.IEmailMessageSender;
import strata.server.core.notification.SerializableEmailMessage;

import java.util.ArrayList;
import java.util.List;

public
class MockEmailMessageSender
    implements IEmailMessageSender
{
    private final ObjectMapper        mapper;
    private final List<IEmailMessage> messages;
    private final Logger              logger;

    public
    MockEmailMessageSender()
    {
        this.mapper = new ObjectMapperProvider().get();
        messages = new ArrayList<>();
        logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public IEmailMessageSender
    open()
    {
        return this;
    }

    @Override
    public IEmailMessageSender
    close()
    {
        return this;
    }

    @Override
    public boolean
    isOpen()
    {
        return true;
    }

    @Override
    public IEmailMessageSender
    send(IEmailMessage message)
    {
        try
        {
            logger.info(
                    "Sending email message: {}",
                        mapper.writeValueAsString(
                            SerializableEmailMessage.of(message)));
            messages.add(message);
            return this;
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<IEmailMessage>
    getMessages() { return messages; }

    public IEmailMessage
    getFirstMessage()
    {
        return messages.isEmpty() ? null : messages.get(0);
    }

    public IEmailMessage
    getLastMessage()
    {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }
}

//////////////////////////////////////////////////////////////////////////////
