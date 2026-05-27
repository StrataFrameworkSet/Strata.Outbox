//////////////////////////////////////////////////////////////////////////////
// PlatformConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.core.inject.IInjector;
import strata.foundation.spring.inject.OperationScoped;
import strata.foundation.spring.inject.SingletonScoped;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.IOutboxEventProcessor;
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
    outboxWorker(IInjector injector,IOutboxEventRouter router)
    {
        return new PollingOutboxWorker(injector,router);
    }

    @Bean
    @OperationScoped
    public IOutboxEventProcessor
    processor(IOutboxEventRepository repository,IOutboxEventRouter router)
    {
        return new OutboxEventProcessor(repository,router);
    }
}

//////////////////////////////////////////////////////////////////////////////
