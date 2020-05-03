package net.purevirtual.chell.central.web.agent.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardMove;
import net.purevirtual.chell.central.web.crud.entity.enums.HybridType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HybridAgent implements IAgent {
    private static final Logger logger = LoggerFactory.getLogger(HybridAgent.class);
    
    private List<HybridSubAgent> subagents;
    private Engine engine;
    public HybridAgent(Engine engine, List<HybridSubAgent> subagents) {
        if(subagents.isEmpty()) {
            throw new IllegalArgumentException("Hybrid agent requires subAgents");
        }
        this.engine = engine;
        this.subagents = subagents;
    }
    
    public static class HybridSubAgent {
        public IAgent agent;
        public EngineConfig engineConfig;

        public HybridSubAgent(IAgent agent, EngineConfig engineConfig) {
            this.agent = agent;
            this.engineConfig = engineConfig;
        }

        @Override
        public String toString() {
            return "HybridSubAgent{" + "agent=" + agent + ", engineConfig=" + engineConfig + '}';
        }
        
    }

    @Override
    public synchronized boolean assignGame(LiveGame game) throws InterruptedException {
        logger.info("assigning game {}", game);
        boolean success = true;
        for (HybridSubAgent subagent : subagents) {
            success = success && subagent.agent.assignGame(game);
        }
        if(success) {
            logger.info("assigned game to subEngines {}", subagents);
            return true;
        } else {
            for (HybridSubAgent subagent : subagents) {
                subagent.agent.release(game);
            }    
            return false;
        }
    }
    
    @Override
    public synchronized Optional<LiveGame> getLiveGame() {
        return subagents.get(0).agent.getLiveGame();
    }
    
    @Override
    public synchronized void release(LiveGame game) {
        for (HybridSubAgent subagent : subagents) {
            subagent.agent.release(game);
        }
    }

    @Override
    public CompletableFuture<BoardMove> move(List<String> movesSoFar, long moveTimeLimit) {
        //TODO: dodać inne tryby wyboru
        List<CompletableFuture<BoardMove>> futures = new ArrayList<>();
        for (HybridSubAgent subagent : subagents) {
            futures.add(subagent.agent.move(movesSoFar, moveTimeLimit));
        }
        return CompletableFuture.supplyAsync(()-> {
            CompletableFuture.allOf(toArray(futures));
            try {
                List<BoardMove> moves = new ArrayList<>();
                for (CompletableFuture<BoardMove> future : futures) {
                    moves.add(future.get());
                }
                BoardMove selectMove = new HybridSelect().selectMove(HybridType.VOTE_ELO, moves, subagents);
                logger.info("Selected move {} out of {}", selectMove, moves);
                return selectMove;
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Void> reset(EngineConfig engineConfig) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (HybridSubAgent subagent : subagents) {
            futures.add(subagent.agent.reset(subagent.engineConfig));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()]));
    }

    @Override
    public int getId() {
        return engine.getId();
    }
    
    private static <T> CompletableFuture<T>[] toArray(List<CompletableFuture<T>> futures) {
        return futures.toArray((CompletableFuture<T>[]) new CompletableFuture<?>[futures.size()]);
    }
    
}
