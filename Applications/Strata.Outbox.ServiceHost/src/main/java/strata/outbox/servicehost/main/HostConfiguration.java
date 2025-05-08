//////////////////////////////////////////////////////////////////////////////
// HostConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.servicehost.main;

import org.springframework.scheduling.annotation.EnableAsync;
import strata.outbox.core.receiver.EmailMessageOutboxEventReceiver;
import strata.outbox.core.receiver.IOutboxEventReceiverMapProvider;
import strata.outbox.core.receiver.TextMessageOutboxEventReceiver;
import strata.outbox.core.shared.MockEmailMessageSender;
import strata.outbox.core.shared.MockTextMessageSender;
import strata.outbox.server.configuration.StrataOutboxServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.ITextMessage;

import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableAsync
public
class HostConfiguration
    extends StrataOutboxServerConfiguration
{
    @Override
    @Bean
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
