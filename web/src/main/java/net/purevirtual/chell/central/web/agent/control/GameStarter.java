package net.purevirtual.chell.central.web.agent.control;

import java.util.Optional;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.entity.Agent;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
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
        EngineConfig player1 = match.getPlayer1();
        
        Optional<UciAgent> agentOpt1 = liveAgentsManager.find(player1.getAgent());
        if (agentOpt1.isEmpty()) {
            logger.info("Cannot start game {} from match {}, the agent {} is not online", game.getId(), match.getId(), player1.getAgent().getId());
            return;
        }
        EngineConfig player2 = match.getPlayer1();
        Optional<UciAgent> agentOpt2 = liveAgentsManager.find(player2.getAgent());
        if (agentOpt2.isEmpty()) {
            logger.info("Cannot start game {} from match {}, the agent {} is not online", game.getId(), match.getId(), player2.getAgent().getId());
            return;
        }
        UciAgent agent1 = agentOpt1.get();
        UciAgent agent2 = agentOpt2.get();
        LiveGame liveGame;
        if (game.isWhitePlayedByFirstAgent()) {
            liveGame = new LiveGame(game, agent1, agent2, player1, player2);
        } else {
            liveGame = new LiveGame(game, agent2, agent1, player2, player1);
        }
        
        gameRunner.run(liveGame);
        
//        try {
//            
//        }

    }
}
