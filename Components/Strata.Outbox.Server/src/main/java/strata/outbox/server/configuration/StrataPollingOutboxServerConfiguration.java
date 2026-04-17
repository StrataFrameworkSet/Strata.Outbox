//////////////////////////////////////////////////////////////////////////////
// StrataOutboxServerConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.configuration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.spring.inject.SingletonScoped;
import strata.outbox.core.receiver.EmailMessageOutboxEventReceiver;
import strata.outbox.core.receiver.IOutboxEventReceiverMapProvider;
import strata.outbox.core.receiver.TextMessageOutboxEventReceiver;
import strata.outbox.server.application.ApplicationConfiguration;
import strata.outbox.server.application.IOutboxWorker;
import strata.outbox.server.domain.DomainConfiguration;
import strata.outbox.server.platform.PollingPlatformConfiguration;
import strata.server.core.inject.SecureEmailConfigurationProvider;
import strata.server.core.inject.SecureTextingConfigurationProvider;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.ITextMessage;
import strata.server.core.notification.JavaMailMessageSender;
import strata.server.core.notification.TeleSignMessageSender;

import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableAsync
@Import({
    ApplicationConfiguration.class,
    DomainConfiguration.class,
    PollingPlatformConfiguration.class,
})
public
class StrataPollingOutboxServerConfiguration
{
    @Bean
    @SingletonScoped
    public IOutboxEventReceiverMapProvider
    receiverMapProvider(IConfiguration configuration)
    {
        return
            () ->
                Map.of(
                    IEmailMessage
                        .class
                        .getSimpleName(),
                    new EmailMessageOutboxEventReceiver(
                        new JavaMailMessageSender(
                            new SecureEmailConfigurationProvider(
                                configuration))),
                    ITextMessage
                        .class
                        .getSimpleName(),
                    new TextMessageOutboxEventReceiver(
                        new TeleSignMessageSender(
                            new SecureTextingConfigurationProvider(
                                configuration))));
    }

    @Bean
    public InitializingBean
    initialize(IOutboxWorker worker)
    {
        return () -> worker.start();
    }
}

//////////////////////////////////////////////////////////////////////////////
