//////////////////////////////////////////////////////////////////////////////
// HostConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.servicehost.main;

import strata.outbox.server.application.ApplicationConfiguration;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.DomainConfiguration;
import strata.outbox.server.platform.PlatformConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.foundation.spring.mapper.StrataModelResolver;
import strata.foundation.spring.mapper.StrataObjectMapperProvider;
import strata.server.spring.service.ServiceConfiguration;

@Configuration
@EnableTransactionManagement
@Import({
    ApplicationConfiguration.class,
    DomainConfiguration.class,
    PlatformConfiguration.class,
    ServiceConfiguration.class,
    SpringDocConfigProperties.class,
    StrataModelResolver.class})
public
class HostConfiguration
{
    @Bean
    public InitializingBean
    initialize()
    {
        return
            () ->
                SecurityContextHolder
                    .setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public BeanFactoryPostProcessor
    beanFactoryPostProcessor()
    {
        return
            factory ->
                factory.registerScope(
                    "request",
                    new RequestScope());
    }

    @Bean
    @Scope("singleton")
    public IConfiguration
    configuration()
    {
        return
            new ApplicationConfigurationProvider("development").get();
    }

    @Bean
    public CommonsRequestLoggingFilter
    requestLoggingFilter()
    {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }

}

//////////////////////////////////////////////////////////////////////////////
