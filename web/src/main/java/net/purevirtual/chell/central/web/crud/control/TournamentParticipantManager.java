package net.purevirtual.chell.central.web.crud.control;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.TournamentParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class TournamentParticipantManager {

    private static final Logger logger = LoggerFactory.getLogger(TournamentParticipantManager.class);

    @PersistenceContext
    private EntityManager entityManager;


    public TournamentParticipant save(TournamentParticipant tournamentParticipant) {
        entityManager.persist(tournamentParticipant);
        return tournamentParticipant;
    }


}
