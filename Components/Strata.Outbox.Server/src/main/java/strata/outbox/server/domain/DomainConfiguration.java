/// ///////////////////////////////////////////////////////////////////////////
// DomainConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.outbox.core.receiver.IOutboxEventReceiverMapProvider;
import strata.outbox.core.repository.RepositoryConfiguration;

@Configuration
@EnableTransactionManagement
@Import({RepositoryConfiguration.class})
public
class DomainConfiguration
{
    @Bean
    @Scope("singleton")
    public IOutboxEventRouter
    router(IOutboxEventReceiverMapProvider provider)
    {
        return new OutboxEventRouter(provider.get());
    }
}

//////////////////////////////////////////////////////////////////////////////
