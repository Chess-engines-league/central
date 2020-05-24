package net.purevirtual.chell.central.web.agent.control;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardMove;

public interface IAgent {
    /**
     *
     * @param game game to join
     * @param reentrant - is joining the same game again allowed
     * @return true if agent joined the game
     * @throws InterruptedException
     */
    boolean assignGame(LiveGame game) throws InterruptedException;
    Optional<LiveGame> getLiveGame();
    void release(LiveGame game);
    
    CompletableFuture<BoardMove> move(List<String> movesSoFar, long moveTimeLimit);
    CompletableFuture<Void> reset(EngineConfig engineConfig);
    
    int getId(); 
}
