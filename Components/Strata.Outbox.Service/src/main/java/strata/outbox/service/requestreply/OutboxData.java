//////////////////////////////////////////////////////////////////////////////
// OutboxData.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.service.requestreply;

public
class OutboxData
{
    private Long   outboxId;
    private String foo;

    public
    OutboxData()
    {
        outboxId = null;
        foo = null;
    }

    public OutboxData
    setOutboxId(Long id)
    {
        outboxId = id;
        return this;
    }

    public OutboxData
    setFoo(String f)
    {
        foo = f;
        return this;
    }

    public Long
    getOutboxId() { return outboxId; }
    
    public String
    getFoo() { return foo; }
}

//////////////////////////////////////////////////////////////////////////////
