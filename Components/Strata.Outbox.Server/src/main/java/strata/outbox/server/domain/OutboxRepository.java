//////////////////////////////////////////////////////////////////////////////
// OutboxRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.server.domain;

import strata.server.core.repository.AbstractRepository;
import strata.server.core.unitofwork.IUnitOfWork;

public
class OutboxRepository
    extends AbstractRepository<Long,Outbox>
    implements IOutboxRepository
{
    public
    OutboxRepository(IUnitOfWork uow)
    {
        super(Outbox.class,"primaryId",uow);
    }

}

//////////////////////////////////////////////////////////////////////////////
