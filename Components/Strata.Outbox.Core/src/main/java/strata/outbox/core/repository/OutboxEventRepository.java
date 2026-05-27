//////////////////////////////////////////////////////////////////////////////
// OutboxEventRepository.java
//////////////////////////////////////////////////////////////////////////////

package strata.outbox.core.repository;

import jakarta.inject.Inject;
import strata.server.core.repository.AbstractRepository;
import strata.server.core.unitofwork.IUnitOfWork;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public
class OutboxEventRepository
    extends AbstractRepository<UUID,OutboxEvent>
    implements IOutboxEventRepository
{
    private static final String FIND_BY_STATUS_ORDERED =
        "select e from OutboxEvent e where e.status = :status order by e.created asc";

    @Inject
    public
    OutboxEventRepository(IUnitOfWork uow)
    {
        super(OutboxEvent.class,"id",uow);
        getUnitOfWork()
            .registerQuery("findByStatusOrdered",FIND_BY_STATUS_ORDERED);
    }

    @Override
    public List<OutboxEvent>
    findAllByStatus(OutboxEventStatus status)
    {
        return
            getUnitOfWork()
                .findManyByCriteria(OutboxEvent.class,"status",status);
    }

    @Override
    public List<OutboxEvent>
    findAllByStatusOrderByCreated(OutboxEventStatus status,int limit)
    {
        return
            getUnitOfWork()
                .findManyByQuery(
                    OutboxEvent.class,
                    "findByStatusOrdered",
                    Map.of("status",status))
                .stream()
                .limit(limit)
                .toList();
    }
}

//////////////////////////////////////////////////////////////////////////////
