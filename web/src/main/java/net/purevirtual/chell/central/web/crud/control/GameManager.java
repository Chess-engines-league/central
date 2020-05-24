package net.purevirtual.chell.central.web.crud.control;

import java.time.Duration;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardState;
import net.purevirtual.chell.central.web.crud.entity.dto.ResultAndReason;
import net.purevirtual.chell.central.web.crud.entity.enums.Player;
import net.purevirtual.chell.central.web.crud.entity.enums.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class GameManager {

    private static final Logger logger = LoggerFactory.getLogger(GameManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Game get(Integer id) {
        return entityManager.find(Game.class, id);
    }

    public Game save(Game game) {
        entityManager.persist(game);
        return game;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Asynchronous
    public void updateBoardState(Game game, BoardState moves, Duration moveDuration, Side side) {
        String json = moves.toJson();
        int p1Inc = 0;
        int p2Inc = 0;
        if(game.getPlayer(side)==Player.PLAYER1) {
            p1Inc = moveDuration.toMillisPart();
        } else {
            p2Inc = moveDuration.toMillisPart();
        }
        entityManager.createQuery("update Game g set g.boardState = :boardState, g.clock1ms = g.clock1ms + :p1Inc, g.clock2ms=g.clock2ms+:p2Inc where g.id = :id")
                .setParameter("id", game.getId())
                .setParameter("boardState", json)
                .setParameter("p1Inc", p1Inc)
                .setParameter("p2Inc", p2Inc)
                .executeUpdate();
        entityManager.refresh(game);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateGameResult(Game game, ResultAndReason resultAndReason) {
        entityManager.createQuery("update Game g set g.result = :result, g.reason=:reason where g.id = :id")
                .setParameter("id", game.getId())
                .setParameter("result", resultAndReason.getResult())
                .setParameter("reason", resultAndReason.getReason())
                .executeUpdate();    
    }

    public List<Game> findByMatch(Match match) {
        return entityManager.createQuery("select g from Game g where g.match = :match order by g.gameNumber", Game.class)
                .setParameter("match", match)
                .getResultList();
    }
    
    public List<Game> findAll() {
        return entityManager.createQuery("select g from Game g order by g.id desc", Game.class)
                .getResultList();
    }

}
