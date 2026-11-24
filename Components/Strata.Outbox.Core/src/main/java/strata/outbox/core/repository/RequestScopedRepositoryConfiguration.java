//////////////////////////////////////////////////////////////////////////////
// RequestScopedRepositoryConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import strata.foundation.spring.inject.RequestScoped;
import strata.server.core.unitofwork.IUnitOfWork;

@Configuration
public
class RequestScopedRepositoryConfiguration
{
    @Bean
    @RequestScoped
    public IOutboxEventRepository
    outboxEventRepository(IUnitOfWork unitOfWork)
    {
        return new OutboxEventRepository(unitOfWork);
    }
}

//////////////////////////////////////////////////////////////////////////////
