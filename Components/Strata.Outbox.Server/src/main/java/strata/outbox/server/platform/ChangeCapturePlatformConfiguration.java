//////////////////////////////////////////////////////////////////////////////
// PlatformConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.DebeziumEngine.Builder;
import io.debezium.engine.format.Json;
import jakarta.persistence.EntityManagerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.spring.inject.SingletonScoped;
import strata.foundation.spring.mapper.StrataObjectMapperProvider;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.IOutboxEventRouter;
import strata.outbox.service.requestreply.IOutboxService;
import strata.server.spring.repository.LocalContainerEntityManagerFactoryBeanProvider;
import strata.server.spring.unitofwork.ISpringUnitOfWorkManager;
import strata.server.spring.unitofwork.JpaUnitOfWork;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;

@Configuration
@EnableTransactionManagement
@EnableAsync
public
class ChangeCapturePlatformConfiguration
    extends PlatformConfiguration
{
    @Bean
    @SingletonScoped
    public IOutboxWorker
    outboxWorker(
        Builder<ChangeEvent<String,String>> builder,
        IChangeEventProcessor processor)
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
}

//////////////////////////////////////////////////////////////////////////////
