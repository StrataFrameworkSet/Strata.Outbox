//////////////////////////////////////////////////////////////////////////////
// TestConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.shared;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestScope;
import strata.outbox.core.receiver.EmailMessageOutboxEventReceiver;
import strata.outbox.core.receiver.IOutboxEventReceiverMapProvider;
import strata.outbox.core.receiver.TextMessageOutboxEventReceiver;
import strata.outbox.core.shared.MockTextMessageSender;
import strata.outbox.server.configuration.StrataOutboxServerConfiguration;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.ITextMessage;
import strata.outbox.core.shared.MockEmailMessageSender;

import java.util.Map;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:test.properties")
public
class TestConfiguration
    extends StrataOutboxServerConfiguration
{
    @Override
    @Bean
    @Scope("singleton")
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
                        new MockEmailMessageSender()),
                    ITextMessage
                        .class
                        .getSimpleName(),
                    new TextMessageOutboxEventReceiver(
                        new MockTextMessageSender()));

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
            new ApplicationConfigurationProvider("test").get();
    }


}

//////////////////////////////////////////////////////////////////////////////
