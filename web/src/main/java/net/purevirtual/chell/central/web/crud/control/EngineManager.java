package net.purevirtual.chell.central.web.crud.control;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class EngineManager {

    private static final Logger logger = LoggerFactory.getLogger(EngineManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Engine get(Integer id) {
        return entityManager.find(Engine.class, id);
    }

    public Optional<Engine> findByToken(String token) {
        return entityManager.createQuery("select e from Engine e where e.token=:token", Engine.class)
                .setParameter("token", token)
                .getResultList().stream().findFirst();
    }
    
    public List<Engine> findAll() {
        return entityManager.createQuery("select e from Engine e", Engine.class)
                .getResultList();
    }

}
