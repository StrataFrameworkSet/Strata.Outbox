//////////////////////////////////////////////////////////////////////////////
// PlatformConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.spring.inject.SingletonScoped;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.IOutboxEventRouter;

@Configuration
@EnableTransactionManagement
@EnableAsync
public
class PollingPlatformConfiguration
    extends PlatformConfiguration
{
    @Bean
    @SingletonScoped
    public IOutboxWorker
    outboxWorker(IOutboxEventRouter router,IOutboxEventRepository repository)
    {
        return new PollingOutboxWorker(router, repository);
    }
}

//////////////////////////////////////////////////////////////////////////////
