/// ///////////////////////////////////////////////////////////////////////////
// IChangeEventProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public
interface IChangeEventProcessor
{
    @Transactional
    void
    process(ChangeEvent<String,String> event);
}

//////////////////////////////////////////////////////////////////////////////