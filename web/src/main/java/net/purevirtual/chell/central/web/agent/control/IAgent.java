package net.purevirtual.chell.central.web.agent.control;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;

public interface IAgent {

    boolean assignGame(LiveGame game) throws InterruptedException;
    void release(LiveGame game);
    
    CompletableFuture<String> move(List<String> movesSoFar, long moveTimeLimit);
    CompletableFuture<Void> reset(EngineConfig engineConfig);
    
    int getId(); 
}
