package net.purevirtual.chell.central.web.crud.control;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.Agent;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class MatchManager {

    private static final Logger logger = LoggerFactory.getLogger(MatchManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Agent get(Integer id) {
        return entityManager.find(Agent.class, id);
    }

    public Match save(Match match) {
        entityManager.persist(match);
        return match;
    }

    public List<Match> findUnfinished() {
        return entityManager.createQuery("select distinct m from Match m  join FETCH m.games where m.state=:state", Match.class)
                .setParameter("state", MatchState.PENDING)
                .getResultList();
    }

}
