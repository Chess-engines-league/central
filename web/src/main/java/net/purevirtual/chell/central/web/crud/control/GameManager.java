package net.purevirtual.chell.central.web.crud.control;

import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
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
    public void updateBoardState(Game game, List<String> moves) {
        entityManager.createQuery("update Game g set g.boardState = :boardState where g.id = :id")
                .setParameter("id", game.getId())
                .setParameter("boardState", String.join(" ", moves))
                .executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateGameResult(Game game, GameResult gameResult) {
        entityManager.createQuery("update Game g set g.result = :result where g.id = :id")
                .setParameter("id", game.getId())
                .setParameter("result", gameResult)
                .executeUpdate();    
    }

}
