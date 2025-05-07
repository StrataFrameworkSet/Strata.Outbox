/// ///////////////////////////////////////////////////////////////////////////
// MockTextMessageSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import strata.server.core.notification.ITextMessage;
import strata.server.core.notification.ITextMessageSender;
import strata.server.core.notification.SerializableTextMessage;

import java.util.ArrayList;
import java.util.List;

public
class MockTextMessageSender
    implements ITextMessageSender
{
    private final ObjectMapper       mapper;
    private final List<ITextMessage> messages;

    public
    MockTextMessageSender()
    {
        this.mapper = new ObjectMapperProvider().get();
        this.messages = new ArrayList<>();
    }

    @Override
    public ITextMessageSender
    open()
    {
        return this;
    }

    @Override
    public ITextMessageSender
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
    public ITextMessageSender
    send(ITextMessage message)
    {
        try
        {
            System
                .out
                .println(
                    "Sending text message: " +
                        mapper.writeValueAsString(
                            SerializableTextMessage.of(message)));
            messages.add(message);
            return this;
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<ITextMessage>
    getMessages() { return messages; }

    public ITextMessage
    getFirstMessage()
    {
        return messages.isEmpty() ? null : messages.get(0);
    }

    public ITextMessage
    getLastMessage()
    {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }

}

//////////////////////////////////////////////////////////////////////////////
