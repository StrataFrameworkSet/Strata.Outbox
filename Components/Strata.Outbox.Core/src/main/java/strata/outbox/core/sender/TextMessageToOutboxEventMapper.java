/// ///////////////////////////////////////////////////////////////////////////
// EmailMessageToOutboxEventMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.sender;

import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.server.core.notification.*;

import java.util.UUID;

public
class TextMessageToOutboxEventMapper
    extends ObjectMapperBasedSourceToOutboxEventMapper
    implements ISourceToOutboxEventMapper<ITextMessage>
{
    public
    TextMessageToOutboxEventMapper() {}

    @Override
    public OutboxEvent
    map(ITextMessage source)
        throws MappingException
    {
        SerializableTextMessage message =
            new SerializableTextMessage()
                .setRecipients(source.getRecipients())
                .setContent(source.getContent());

        try
        {
            return
                new OutboxEvent()
                    .setSourceId(
                        UUID
                            .randomUUID()
                            .toString())
                    .setSourceType(
                        ITextMessage
                            .class
                            .getSimpleName())
                    .setEventType(
                        ITextMessage
                            .class
                            .getSimpleName())
                    .setEventPayload(getMapper().writeValueAsString(message));
        }
        catch (Exception e)
        {
            throw new MappingException(e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
