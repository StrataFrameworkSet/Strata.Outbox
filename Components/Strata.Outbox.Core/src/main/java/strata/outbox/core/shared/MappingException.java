//////////////////////////////////////////////////////////////////////////////
// MappingException.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.shared;

public
class MappingException
    extends RuntimeException
{
    public
    MappingException(String message)
    {
        super(message);
    }

    public
    MappingException(String message,Throwable cause)
    {
        super(message,cause);
    }

    public
    MappingException(Throwable cause)
    {
        super(cause);
    }
}

//////////////////////////////////////////////////////////////////////////////

