/// ///////////////////////////////////////////////////////////////////////////
// DomainConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.outbox.core.receiver.IOutboxEventReceiver;
import strata.outbox.core.repository.RepositoryConfiguration;

import java.util.Map;

@Configuration
@EnableTransactionManagement
@Import({RepositoryConfiguration.class})
public
class DomainConfiguration
{
    @Bean
    public IOutboxEventRouter
    outboxEventRouter(Map<String,IOutboxEventReceiver> receivers)
    {
        return new OutboxEventRouter(receivers);
    }
}

//////////////////////////////////////////////////////////////////////////////
