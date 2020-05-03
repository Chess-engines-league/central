package net.purevirtual.chell.central.web.agent.bounduary;

import java.util.List;
import javax.ejb.Asynchronous;
import net.purevirtual.chell.central.web.agent.control.*;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MatchRunner {

    private static final Logger logger = LoggerFactory.getLogger(MatchRunner.class);

    @Inject
    private GameStarter gameStarter;

    @Inject
    private MatchManager matchManager;

    @Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
    public void checkForUnfinishedMatched() {
        List<Match> unfinished = matchManager.findUnfinished();
        logger.info("found {} unfinished matches", unfinished.size());
        unfinished.parallelStream().forEach(match -> {
            match.getGames().stream().filter(g -> g.getResult() == GameResult.PENDING)
                    .findFirst()
                    .ifPresent(game -> {
                        gameStarter.tryStart(match, game);
                    });
        });
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Schedule(hour = "*", minute = "*", second = "5/10", persistent = false)
    public void checkForFinishedMatched() {
        List<Match> unfinished = matchManager.findUnfinished();
        logger.info("found {} unfinished matches", unfinished.size());
        unfinished.parallelStream().forEach(match -> {
            if (!match.getGames().stream().anyMatch(g -> g.getResult() == GameResult.PENDING)) {
                completeMatch(match);
            }
        });
    }

    @Asynchronous
    public void wake() {
        checkForUnfinishedMatched();
    }

    private void completeMatch(Match match) {
        match.setState(MatchState.FINISHED);
        match.setScore1(match.getGames().stream().map(g->g.getPlayer1Score()).reduce(0, Integer::sum));
        match.setScore2(match.getGames().stream().map(g->g.getPlayer2Score()).reduce(0, Integer::sum));
        int elo1 = match.getPlayer1().getElo();
        int elo2 = match.getPlayer2().getElo();
        double r1 = Math.pow(10, elo1/400);
        double r2 = Math.pow(10, elo2/400);
        double e1 = r1 / (r1+r2);
        double e2 = r2 / (r1+r2);
        double s1 = 0.5d * match.getScore1() / match.getGameCount();
        double s2 = 0.5d * match.getScore2() / match.getGameCount();
        
        int newElo1 = (int) Math.round(elo1 + 32 * (s1 - e1));
        int newElo2 = (int) Math.round(elo2 + 32 * (s2 - e2));
        updateElo(match, match.getPlayer1(), newElo1);
        updateElo(match, match.getPlayer2(), newElo2);
        match.setResult("Player1: " + match.getScore1() + ", Player2:" + match.getScore2());
    }

    private void updateElo(Match match, EngineConfig player, int newElo) {
        logger.info("Changing elo from {} to {} for {} after match {}", 
                player.getElo(), newElo, player.getId(), match.getId());
        player.setElo(newElo);
    }
}
