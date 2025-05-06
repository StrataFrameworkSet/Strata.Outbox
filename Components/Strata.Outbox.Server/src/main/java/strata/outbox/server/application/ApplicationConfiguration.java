//////////////////////////////////////////////////////////////////////////////
// ApplicationConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.event.IOutboxEventSender;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@EnableTransactionManagement
public
class ApplicationConfiguration
{
    @Bean
    @RequestScope
    public ITransactionalOutboxService
    sessionService(
        ModelMapper            mapper)
    {
        return new OutboxService(null,null,mapper);
    }

    @Bean
    @Scope("singleton")
    public ModelMapper
    mapper()
    {
        return new ModelMapper().registerModule(new MappingModule());
    }
}

//////////////////////////////////////////////////////////////////////////////
