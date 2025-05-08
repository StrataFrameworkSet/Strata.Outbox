//////////////////////////////////////////////////////////////////////////////
// OutboxServiceHost.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.servicehost.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class,SecurityAutoConfiguration.class})
@EnableTransactionManagement
public
class OutboxServiceHost
{
    public static void
    main(String[] args)
    {
        SpringApplication application =
            new SpringApplication(OutboxServiceHost.class);

        application.setAdditionalProfiles("production");
        application.run(args);
    }
}

//////////////////////////////////////////////////////////////////////////////
