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
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
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

    @Asynchronous
    public void wake() {
        checkForUnfinishedMatched();
    }

}
