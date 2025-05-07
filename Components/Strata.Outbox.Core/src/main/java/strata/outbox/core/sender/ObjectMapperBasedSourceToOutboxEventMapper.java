/// ///////////////////////////////////////////////////////////////////////////
// ObjectMapperBasedSourceToOutboxEventMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.sender;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import strata.outbox.core.shared.ObjectMapperProvider;

public abstract
class ObjectMapperBasedSourceToOutboxEventMapper
{
    private final ObjectMapper mapper;

    protected
    ObjectMapperBasedSourceToOutboxEventMapper()
    {
        mapper = new ObjectMapperProvider().get();
    }

    protected ObjectMapper
    getMapper() { return mapper; }
}

//////////////////////////////////////////////////////////////////////////////
