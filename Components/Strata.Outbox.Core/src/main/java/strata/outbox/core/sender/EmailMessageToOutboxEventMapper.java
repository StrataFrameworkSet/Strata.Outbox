/// ///////////////////////////////////////////////////////////////////////////
// EmailMessageToOutboxEventMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.SerializableAttachment;
import strata.server.core.notification.SerializableEmailMessage;

import java.io.Serializable;
import java.util.UUID;
import java.util.stream.Collectors;

public
class EmailMessageToOutboxEventMapper
    extends ObjectMapperBasedSourceToOutboxEventMapper
    implements ISourceToOutboxEventMapper<IEmailMessage>
{
    public
    EmailMessageToOutboxEventMapper() {}

    @Override
    public OutboxEvent
    map(IEmailMessage source)
        throws MappingException
    {
        SerializableEmailMessage message =
            new SerializableEmailMessage()
                .setSender(source.getSender())
                .setRecipients(source.getRecipients())
                .setSubject(source.getSubject())
                .setContent(source.getContent())
                .setAttachments(
                    source
                        .getAttachments()
                        .stream()
                        .map(
                            attachment ->
                                new SerializableAttachment()
                                    .setContentId(attachment.getContentId())
                                    .setContentType(attachment.getContentType())
                                    .setBytes(attachment.getBytes()))
                        .collect(Collectors.toSet()));

        try
        {
            return
                new OutboxEvent()
                    .setSourceId(
                        UUID
                            .randomUUID()
                            .toString())
                    .setSourceType(
                        IEmailMessage
                            .class
                            .getSimpleName())
                    .setEventType(
                        IEmailMessage
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
