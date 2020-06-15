package net.purevirtual.chell.central.web.crud.control;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.Tournament;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class TournamentManager {

    private static final Logger logger = LoggerFactory.getLogger(TournamentManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Tournament get(Integer id) {
        return entityManager.createQuery("select t from Tournament t LEFT JOIN FETCH t.participants where t.id=:id", Tournament.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Tournament save(Tournament tournament) {
        entityManager.persist(tournament);
        return tournament;
    }

    public List<Tournament> findUnfinished() {
        return entityManager.createQuery("select distinct t from Tournament t  join FETCH t.matches where t.state=:state", Tournament.class)
                .setParameter("state", MatchState.PENDING)
                .getResultList();
    }

    public List<Tournament> findAll() {
        return entityManager.createQuery("select t from Tournament t order by t.id desc", Tournament.class)
                .getResultList();
    }

}
