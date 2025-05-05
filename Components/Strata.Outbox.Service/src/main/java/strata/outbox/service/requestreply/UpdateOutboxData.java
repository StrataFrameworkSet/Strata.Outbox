//////////////////////////////////////////////////////////////////////////////
// UpdateOutboxData.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

import java.util.Optional;

public
class UpdateOutboxData
{
    private Long   outboxId;
    private String foo;

    public
    UpdateOutboxData()
    {
        outboxId = null;
        foo = null;
    }

    public UpdateOutboxData
    setOutboxId(Long id)
    {
        outboxId = id;
        return this;
    }

    public UpdateOutboxData
    setFoo(String f)
    {
        foo = f;
        return this;
    }

    public Long
    getOutboxId() { return outboxId; }

    public Optional<String>
    getFoo() { return Optional.ofNullable(foo); }
}

//////////////////////////////////////////////////////////////////////////////
