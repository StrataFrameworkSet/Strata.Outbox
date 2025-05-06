//////////////////////////////////////////////////////////////////////////////
// MappingModule.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.application;

import strata.outbox.service.requestreply.CreateOutboxData;
import strata.outbox.service.requestreply.OutboxData;
import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

public
class MappingModule
    implements Module
{
    @Override
    public void
    setupModule(ModelMapper mapper)
    {
        mapper.createTypeMap(CreateOutboxData.class,Outbox.class);

        mapper
            .createTypeMap(OutboxData.class,Outbox.class)
            .addMapping(OutboxData::getOutboxId,Outbox::setPrimaryId);

        mapper
            .emptyTypeMap(Outbox.class,OutboxData.class)
            .addMapping(Outbox::getPrimaryId,OutboxData::setOutboxId)
            .addMapping(Outbox::getFoo,OutboxData::setFoo);
    }
}

//////////////////////////////////////////////////////////////////////////////
