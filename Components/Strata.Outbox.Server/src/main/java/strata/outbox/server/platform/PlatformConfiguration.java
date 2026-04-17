//////////////////////////////////////////////////////////////////////////////
// PlatformConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import strata.foundation.spring.inject.SingletonScoped;
import strata.foundation.spring.mapper.StrataObjectMapperProvider;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.IOutboxEventRouter;
import strata.outbox.service.requestreply.IOutboxService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.core.configuration.IConfiguration;
import strata.server.spring.health.HealthCheckConfiguration;
import strata.server.spring.repository.LocalContainerEntityManagerFactoryBeanProvider;
import strata.server.spring.unitofwork.ISpringUnitOfWorkManager;
import strata.server.spring.unitofwork.JpaUnitOfWork;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;
import io.debezium.engine.DebeziumEngine.Builder;

@Configuration
@EnableTransactionManagement
@EnableAsync
@Import({HealthCheckConfiguration.class})
public
class PlatformConfiguration
{

    @Bean("outbox-worker")
    @SingletonScoped
    public ThreadPoolTaskExecutor
    executor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(256);
        executor.setQueueCapacity(256);
        return executor;
    }

    @Bean
    @SingletonScoped
    public IOutboxWorker
    outboxWorker(
        Builder<ChangeEvent<String,String>> builder,
        IChangeEventProcessor               processor)
    {
        return new DebeziumOutboxWorker(builder,processor);
    }

    @Bean
    @SingletonScoped
    public Builder<ChangeEvent<String,String>>
    builder(IConfiguration configuration)
    {
        return
            DebeziumEngine
                .create(Json.class)
                .using(new DebeziumPropertiesProvider(configuration).get());
    }

    @Bean
    @SingletonScoped
    public IChangeEventProcessor
    changeEventProcessor(IOutboxEventRouter router,IOutboxEventRepository repository)
    {
        return new RoutedChangeEventProcessor(router,repository);
    }

    @Bean
    public OutboxServiceController
    outboxServiceController(IOutboxService service)
    {
        return new OutboxServiceController(service);
    }

    @Bean
    @SingletonScoped
    public LocalContainerEntityManagerFactoryBean
    localContainerEntityManagerFactoryBean(IConfiguration configuration)
    {
        return
            new LocalContainerEntityManagerFactoryBeanProvider(configuration)
                .get();
    }

    @Bean
    @SingletonScoped
    public ISpringUnitOfWorkManager
    unitOfWorkManager(JpaUnitOfWork unitOfWork)
    {
        return new JpaUnitOfWorkManager(unitOfWork);
    }

    @Bean
    @SingletonScoped
    public JpaUnitOfWork
    unitOfWork(EntityManagerFactory factory)
    {
        return new JpaUnitOfWork(factory);
    }

    @Bean
    @SingletonScoped
    public GroupedOpenApi
    openApi()
    {
        return
            GroupedOpenApi
                .builder()
                .group("Outbox")
                .pathsToMatch(
                    "/outbox-service/**",
                    "/health/**")
                .packagesToScan(
                    "strata.outbox.server.platform",
                    "strata.server.spring.health")
                .build();
    }

    @Bean
    @SingletonScoped
    public ObjectMapperProvider
    objectMapperProvider(SpringDocConfigProperties properties)
    {
        return new StrataObjectMapperProvider(properties);
    }
}

//////////////////////////////////////////////////////////////////////////////
