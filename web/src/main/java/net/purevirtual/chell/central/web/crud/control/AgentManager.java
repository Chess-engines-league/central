package net.purevirtual.chell.central.web.crud.control;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class AgentManager {

    private static final Logger logger = LoggerFactory.getLogger(AgentManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Agent get(Integer id) {
        return entityManager.find(Agent.class, id);
    }

    public Optional<Agent> findByToken(String token) {
        return entityManager.createQuery("select a from Agent a where a.token=:token", Agent.class)
                .setParameter("token", token)
                .getResultList().stream().findFirst();
    }
    
    public List<Agent> findAll() {
        return entityManager.createQuery("select a from Agent a", Agent.class)
                .getResultList();
    }

}
