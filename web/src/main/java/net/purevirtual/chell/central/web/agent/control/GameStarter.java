package net.purevirtual.chell.central.web.agent.control;

import java.util.Optional;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Asynchronous
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class GameStarter {
    private static final Logger logger = LoggerFactory.getLogger(GameStarter.class);

    @Inject
    private GameRunner gameRunner;
    
    @Inject
    private LiveAgentsManager liveAgentsManager;
    
    public void tryStart(Match match, Game game) {
        Optional<UciAgent> agentOpt1 = liveAgentsManager.find(match.getAgent1());
        if (agentOpt1.isEmpty()) {
            logger.info("Cannot start game {} from match {}, the agent {} is not online", game.getId(), match.getId(), match.getAgent1().getId());
            return;
        }
        Optional<UciAgent> agentOpt2 = liveAgentsManager.find(match.getAgent2());
        if (agentOpt2.isEmpty()) {
            logger.info("Cannot start game {} from match {}, the agent {} is not online", game.getId(), match.getId(), match.getAgent1().getId());
            return;
        }
        UciAgent agent1 = agentOpt1.get();
        UciAgent agent2 = agentOpt2.get();
        LiveGame liveGame;
        if (game.isWhitePlayedByFirstAgent()) {
            liveGame = new LiveGame(game, agent1, agent2);
        } else {
            liveGame = new LiveGame(game, agent2, agent1);
        }
        
        gameRunner.run(liveGame);
        
//        try {
//            
//        }

    }
}
