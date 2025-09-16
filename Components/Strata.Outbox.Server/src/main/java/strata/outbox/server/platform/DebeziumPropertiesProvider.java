/// ///////////////////////////////////////////////////////////////////////////
// DebeziumPropertiesProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.platform;

import jakarta.inject.Provider;
import strata.foundation.core.configuration.IConfiguration;

import java.util.Properties;

public
class DebeziumPropertiesProvider
    implements Provider<Properties>
{
    private final IConfiguration configuration;

    public
    DebeziumPropertiesProvider(IConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public Properties
    get()
    {
        Properties properties = new Properties();

        // Basic Debezium engine properties
        properties.setProperty(
            "name",
            getOptional("debezium.name","outbox-worker"));
        properties.setProperty(
            "offset.storage",
            "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        properties.setProperty(
            "offset.storage.file.filename",
            getRequired("debezium.offset.storage.file.filename"));
        properties.setProperty(
            "offset.flush.interval.ms",
            getOptional("debezium.offset.flush.interval.ms","60000"));

        // Database connector configuration
        properties.setProperty(
            "connector.class",
            getOptional(
                "debezium.connector.class",
                "io.debezium.connector.postgresql.PostgresConnector"));
        properties.setProperty(
            "plugin.name",
            getOptional("debezium.plugin.name","pgoutput"));
        properties.setProperty(
            "database.hostname",
            getOptional("debezium.database.hostname","localhost"));
        properties.setProperty(
            "database.port",
            getOptional("debezium.database.port","5432"));
        properties.setProperty(
            "database.user",
            getRequired("debezium.database.user"));
        properties.setProperty(
            "database.password",
            getRequired("debezium.database.password"));
        properties.setProperty(
            "database.dbname",
            getRequired("debezium.database.dbname"));
        properties.setProperty(
            "database.server.name",
            getRequired("debezium.database.server.name"));

        // Connection pool properties
        properties.setProperty(
            "connection.pool.size",
            getOptional("debezium.connection.pool.size","5"));
        properties.setProperty(
            "connection.timeout.ms",
            getOptional("debezium.connection.timeout.ms","30000"));
        properties.setProperty(
            "max.connection.attempts",
            getOptional("debezium.max.connection.attempts","3"));

        // Skip operations
        properties.setProperty(
            "skipped.operations",
            getOptional("debezium.skipped.operations","d,t"));
        properties.setProperty("tombstone.on.delete","false");

        // Table inclusion/exclusion configs
        properties.setProperty(
            "table.include.list",
            getOptional("debezium.table.include.list","public.outboxevent"));

        // Outbox specific configurations
        properties.setProperty(
            "topic.prefix",
            getOptional("debezium.topic.prefix","outbox"));
        properties.setProperty(
            "slot.name",
            getOptional("debezium.slot.name","debezium_outbox"));
        properties.setProperty(
            "slot.max.retries",
            getOptional("debezium.max.retries","6"));
        properties.setProperty(
            "slot.retry.delay.ms",
            getOptional("debezium.slot.retry.delay.ms","10000"));
        return properties;
    }

    protected String
    getRequired(String key)
    {
        if (!configuration.hasProperty(key))
            throw new IllegalArgumentException(
                String.format("Missing required property: %s",key));

        return configuration.getProperty(key);
    }

    protected String
    getOptional(String key,String defaultValue)
    {
        return
            configuration.hasProperty(key)
                ? configuration.getProperty(key)
                : defaultValue;
    }
}

//////////////////////////////////////////////////////////////////////////////
