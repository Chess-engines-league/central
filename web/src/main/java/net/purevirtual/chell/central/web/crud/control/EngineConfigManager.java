package net.purevirtual.chell.central.web.crud.control;

import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class EngineConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(EngineConfigManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public EngineConfig get(Integer id) {
        return entityManager.find(EngineConfig.class, id);
    }

    public EngineConfig save(EngineConfig engineConfig) {
        entityManager.persist(engineConfig);
        return engineConfig;
    }

}
