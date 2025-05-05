//////////////////////////////////////////////////////////////////////////////
// SendException.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.sender;

public
class SendException
    extends RuntimeException
{
    public
    SendException(String message)
    {
        super(message);
    }

    public
    SendException(String message,Throwable cause)
    {
        super(message,cause);
    }

    public
    SendException(Throwable cause)
    {
        super(cause);
    }
}

//////////////////////////////////////////////////////////////////////////////

