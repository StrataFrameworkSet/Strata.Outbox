/// ///////////////////////////////////////////////////////////////////////////
// OutboxEvent.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.repository;

import java.io.Serializable;
import java.util.UUID;

public
class OutboxEvent
    implements Serializable
{
    private UUID    id;
    private Integer attempt;
    private String  sourceType;
    private String  sourceId;
    private String  eventType;
    private String  eventPayload;

    public
    OutboxEvent()
    {
        this.id = null;
        this.attempt = 1;
        this.sourceType = null;
        this.sourceId = null;
        this.eventType = null;
        this.eventPayload = null;
    }

    public OutboxEvent
    setId(UUID id)
    {
        this.id = id;
        return this;
    }

    public OutboxEvent
    setAttempt(Integer attempt)
    {
        this.attempt = attempt;
        return this;
    }

    public OutboxEvent
    setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
        return this;
    }

    public OutboxEvent
    setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
        return this;
    }

    public OutboxEvent
    setEventType(String eventType)
    {
        this.eventType = eventType;
        return this;
    }

    public OutboxEvent
    setEventPayload(String eventPayload)
    {
        this.eventPayload = eventPayload;
        return this;
    }

    public UUID
    getId()
    {
        return id;
    }

    public Integer
    getAttempt() { return attempt; }

    public String
    getSourceType()
    {
        return sourceType;
    }

    public String
    getSourceId()
    {
        return sourceId;
    }

    public String
    getEventType()
    {
        return eventType;
    }

    public String
    getEventPayload()
    {
        return eventPayload;
    }

    public OutboxEvent
    incrementAttempt()
    {
        attempt = (attempt == null) ? 1 : attempt + 1;
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
