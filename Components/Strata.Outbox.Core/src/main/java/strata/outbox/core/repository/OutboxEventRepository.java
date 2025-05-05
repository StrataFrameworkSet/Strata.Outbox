//////////////////////////////////////////////////////////////////////////////
// OutboxEventRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.repository;

import strata.server.core.repository.AbstractRepository;
import strata.server.core.unitofwork.IUnitOfWork;

import java.util.UUID;

public
class OutboxEventRepository
    extends AbstractRepository<UUID,OutboxEvent>
    implements IOutboxEventRepository
{
    public
    OutboxEventRepository(IUnitOfWork uow)
    {
        super(OutboxEvent.class,"id",uow);
    }
}

//////////////////////////////////////////////////////////////////////////////
