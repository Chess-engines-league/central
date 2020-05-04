package net.purevirtual.chell.central.web.crud.control;

import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.enums.HybridType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class EngineConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(EngineConfigManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public EngineConfig get(Integer id) {
        EngineConfig find = entityManager.find(EngineConfig.class, id);
        if (find == null) {
            throw new IllegalArgumentException("No such EngineConfig = " + id);
        }
        // todo: fetch join
        find.getHybridEngines().size();
        return find;
    }

    public EngineConfig save(EngineConfig engineConfig) {
        entityManager.persist(engineConfig);
        return engineConfig;
    }

    public List<EngineConfig> findByEngine(int engineId) {
        return entityManager.createQuery("select eg from EngineConfig eg where eg.engine.id=:engineId")
                .setParameter("engineId", engineId)
                .getResultList();
    }

}
