//////////////////////////////////////////////////////////////////////////////
// HostConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.servicehost.main;

import strata.outbox.server.application.ApplicationConfiguration;
import strata.outbox.server.domain.DomainConfiguration;
import strata.outbox.server.platform.PlatformConfiguration;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
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
/*
@EnableWebSecurity
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
 */
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

    /*
    @Bean
    public SessionAuthenticationFilter
    sessionAuthenticationFilter(AuthenticationManager mgr,IJsonWebTokenParser parser)
    {
        return new SessionAuthenticationFilter(mgr,parser);
    }

    @Bean
    public AuthenticationManager
    authenticationManager(
        HttpSecurity http,
        SessionAuthenticationProvider provider)
        throws Exception
    {
        return
            http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }

    @Bean
    public SecurityFilterChain
    securityFilterChain(
        HttpSecurity                http,
        CommonsRequestLoggingFilter requestLoggingFilter,
        SessionAuthenticationFilter sessionAuthenticationFilter)
        throws Exception
    {
        return
            http
                .addFilterBefore(
                    sessionAuthenticationFilter,
                    AnonymousAuthenticationFilter.class)
                .addFilterBefore(
                    requestLoggingFilter,
                    SessionAuthenticationFilter.class)
                .authorizeHttpRequests(
                    authorizer ->
                        authorizer
                            .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/outbox-service/xxx")
                            .permitAll()
                            .requestMatchers(HttpMethod.OPTIONS)
                            .permitAll()
                            .requestMatchers(
                                "/outbox-service/yyy",
                                "/outbox-service/zzz")
                            .hasAuthority("GUEST"))
                .csrf(customizer -> customizer.disable())
                .build();
    }
    */

    @Bean
    @Scope("singleton")
    public GroupedOpenApi
    openApi()
    {
        return
            GroupedOpenApi
                .builder()
                .group("Outbox")
                .pathsToMatch("/outbox-service/**")
                .packagesToScan("strata.outbox.server.platform")
                .build();
    }

    @Bean
    @Scope("singleton")
    public ObjectMapperProvider
    objectMapperProvider(SpringDocConfigProperties properties)
    {
        return new StrataObjectMapperProvider(properties);
    }

    @Bean
    @Scope("singleton")
    public SpringDocConfigProperties
    springDocConfigProperties()
    {
        return new SpringDocConfigProperties();
    }
}

//////////////////////////////////////////////////////////////////////////////
