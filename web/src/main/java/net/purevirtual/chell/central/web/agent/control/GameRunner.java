package net.purevirtual.chell.central.web.agent.control;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import net.purevirtual.chell.central.web.agent.entity.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Asynchronous
public class GameRunner {

    private static final Logger logger = LoggerFactory.getLogger(GameRunner.class);

    public void run(Game game) {
        try {
            logger.info("reseting agents");
            Future<Void> resetW = game.getWhite().reset();
            Future<Void> resetB = game.getBlack().reset();
            resetW.get();
            resetB.get();
            logger.info("agents reseted");
            for (int i = 1; i <= 100; i++) {
                Future<String> moveFuture = game.getWhite().move(game.getMoves());
                String move = moveFuture.get();
                game.getMoves().add(move);
                logger.info("current moves after white {}", game.getMoves());
                moveFuture = game.getBlack().move(game.getMoves());
                move = moveFuture.get();
                game.getMoves().add(move);
                logger.info("current moves after black: {}", game.getMoves());
            }

        } catch (InterruptedException | ExecutionException ex) {
            logger.error("Game faield to complete", ex);
            game.setResult("ERROR");
        }

    }
}
