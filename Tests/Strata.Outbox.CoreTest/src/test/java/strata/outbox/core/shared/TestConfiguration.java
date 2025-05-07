/// ///////////////////////////////////////////////////////////////////////////
// TestConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.outbox.core.repository.RepositoryConfiguration;
import strata.server.spring.repository.SingletonScopeRepositoryConfiguration;

@Configuration
@Import({
    RepositoryConfiguration.class,
    SingletonScopeRepositoryConfiguration.class
})
public
class TestConfiguration
{

    @Bean
    @Scope("singleton")
    public IConfiguration
    configuration()
    {
        return
            new ApplicationConfigurationProvider("test").get();
    }

}

//////////////////////////////////////////////////////////////////////////////
