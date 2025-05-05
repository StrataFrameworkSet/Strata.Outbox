//////////////////////////////////////////////////////////////////////////////
// ReceiveException.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.receiver;

public
class ReceiveException
    extends RuntimeException
{
    public
    ReceiveException(String message)
    {
        super(message);
    }

    public
    ReceiveException(String message,Throwable cause)
    {
        super(message,cause);
    }

    public
    ReceiveException(Throwable cause)
    {
        super(cause);
    }
}

//////////////////////////////////////////////////////////////////////////////

