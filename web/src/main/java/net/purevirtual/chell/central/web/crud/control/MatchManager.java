package net.purevirtual.chell.central.web.crud.control;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.Tournament;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class MatchManager {

    private static final Logger logger = LoggerFactory.getLogger(MatchManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Match get(Integer id) {
        return entityManager.find(Match.class, id);
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

    public List<Match> findAll() {
        return entityManager.createQuery("select m from Match m order by m.id desc", Match.class)
                .getResultList();
    }
    
    public List<Match> findByTournament(Tournament tournament) {
        return entityManager.createQuery("select m from Match m where m.tournament = :tournament order by m.id desc", Match.class)
                .setParameter("tournament", tournament)
                .getResultList();
    }

}
