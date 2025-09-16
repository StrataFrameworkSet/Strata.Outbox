//////////////////////////////////////////////////////////////////////////////
// OutboxEventRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.repository;

import jakarta.inject.Inject;
import strata.server.core.repository.AbstractRepository;
import strata.server.core.unitofwork.IUnitOfWork;

import java.util.UUID;

public
class OutboxEventRepository
    extends AbstractRepository<UUID,OutboxEvent>
    implements IOutboxEventRepository
{
    @Inject
    public
    OutboxEventRepository(IUnitOfWork uow)
    {
        super(OutboxEvent.class,"id",uow);
    }
}

//////////////////////////////////////////////////////////////////////////////
