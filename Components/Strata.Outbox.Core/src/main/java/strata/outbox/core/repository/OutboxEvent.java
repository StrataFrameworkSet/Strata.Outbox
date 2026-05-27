/// ///////////////////////////////////////////////////////////////////////////
// OutboxEvent.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.repository;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public
class OutboxEvent
    implements Serializable
{
    private UUID              id;
    private Long              version;
    private Integer           attempt;
    private String            sourceType;
    private String            sourceId;
    private String            eventType;
    private String            eventPayload;
    private OutboxEventStatus status;
    private Instant           created;
    private Instant           lastModified;

    public
    OutboxEvent()
    {
        Instant now = Instant.now();

        this.id = null;
        this.version = null;
        this.attempt = 1;
        this.sourceType = null;
        this.sourceId = null;
        this.eventType = null;
        this.eventPayload = null;
        this.status = OutboxEventStatus.PENDING;
        this.created = now;
        this.lastModified = now;
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

    public OutboxEvent
    setStatus(OutboxEventStatus status)
    {
        this.status = status;
        return this;
    }

    public OutboxEvent
    setVersion(Long version)
    {
        this.version = version;
        return this;
    }

    public OutboxEvent
    setCreated(Instant created)
    {
        this.created = created;
        return this;
    }

    public OutboxEvent
    setLastModified(Instant lastModified)
    {
        this.lastModified = lastModified;
        return this;
    }

    public UUID
    getId()
    {
        return id;
    }

    public Long
    getVersion()
    {
        return version;
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

    public OutboxEventStatus
    getStatus()
    {
        return status;
    }

    public Instant
    getCreated()
    {
        return created;
    }

    public Instant
    getLastModified()
    {
        return lastModified;
    }

    public OutboxEvent
    incrementAttempt()
    {
        attempt = (attempt == null) ? 1 : attempt + 1;
        return this;
    }
}

//////////////////////////////////////////////////////////////////////////////
