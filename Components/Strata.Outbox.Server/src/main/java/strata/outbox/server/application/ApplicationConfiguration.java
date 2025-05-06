//////////////////////////////////////////////////////////////////////////////
// ApplicationConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.outbox.service.requestreply.IOutboxService;

@Configuration
@EnableTransactionManagement
public
class ApplicationConfiguration
{
    @Bean
    public IOutboxService
    sessionService(IOutboxWorker worker)
    {
        return new OutboxService(worker);
    }

}

//////////////////////////////////////////////////////////////////////////////
