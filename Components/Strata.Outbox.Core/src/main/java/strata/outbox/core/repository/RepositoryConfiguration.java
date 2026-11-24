//////////////////////////////////////////////////////////////////////////////
// RepositoryConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import strata.server.core.unitofwork.IUnitOfWork;

@Deprecated
@Configuration
public
class RepositoryConfiguration 
{
    @Bean
    public IOutboxEventRepository
    outboxEventRepository(IUnitOfWork unitOfWork)
    {
        return new OutboxEventRepository(unitOfWork);
    }
}

//////////////////////////////////////////////////////////////////////////////
