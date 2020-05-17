package net.purevirtual.chell.central.web.agent.control;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UciAgent implements IAgent {
    private static final Logger logger = LoggerFactory.getLogger(UciAgent.class);
    private final AgentInput remote;
    private State state;
    private final Engine agentEntity;
    private LiveGame liveGame;
    private final CompletableFutures<Void> readyFutures = new CompletableFutures();
    private final CompletableFutures<BoardMove> moveFutures = new CompletableFutures<>();
    private LocalDateTime lastMessage = null;
    public UciAgent(AgentInput remote, Engine agentEntity) {
        this.remote = remote;
        this.agentEntity = agentEntity;
        state = State.WAIT_FOR_UCI_OK;
        remote.send("uci");
    }
    
    public void heartbeat(int seconds) {
        if (lastMessage == null ||lastMessage.plusSeconds(seconds).isBefore(LocalDateTime.now())) {
            remote.heartbeat();
        }
    }
    
    @Override
    public CompletableFuture<BoardMove> move(List<String> movesSoFar, long moveTimeLimit) {
        state = State.WAIT_FOR_MOVE;
        if (movesSoFar.isEmpty()) {
            remote.send("position startpos");
        } else {
            remote.send("position startpos moves " + String.join(" ", movesSoFar));
        }
        remote.send("go movetime "+moveTimeLimit);
        return moveFutures.addNew();
    }

    public void onMessage(String message) {
        lastMessage = LocalDateTime.now();
        String[] parts = message.split("\\s+");
        if (parts.length == 0) {
            return;
        }
        String cmd = parts[0];
        switch (cmd) {
            case "bestmove":
                String move = parts[1];
                BoardMove boardMove = new BoardMove();
                boardMove.setMove(move);
                if (parts.length == 4 && "ponder".equals(parts[2])) {
                    boardMove.setPonder(parts[3]);
                } else {
                    String comment = Stream.of(parts).skip(2).collect(Collectors.joining(" "));
                    boardMove.setComment(comment);
                }
                // TODO: make this configurable
                //remote.send("go ponder");
                moveFutures.complete(boardMove);
                break;
            case "info":
                logger.debug(message);
                break;
            case "uciok":
                //sendOptions(engineConfig);
                state = State.WAIT_FOR_READY_OK;
                remote.send("ucinewgame", "isready");
                break;
            case "readyok":
                readyFutures.complete(null);

                break;
            default:
                if (state == State.WAIT_FOR_UCI_OK) {
                    logger.info("uci options : {}" , message);
                } else {
                    logger.info("unknown option : {}", message);
                }

                break;
        }
    } 

    public CompletableFuture<Void> reset(EngineConfig engineConfig) {
        state = State.WAIT_FOR_READY_OK;
        sendOptions(engineConfig);
        remote.send("ucinewgame", "isready");
        return readyFutures.addNew();
        
    }
    
    private void sendOptions(EngineConfig engineConfig) {
        String initOptions = engineConfig.getInitOptions();
        if (initOptions != null) {
            logger.info("initing the game engine with options: {}", initOptions);
            for (String option : initOptions.split("\\R")) {
                String trimmed = option.trim();
                if (!trimmed.isEmpty()) {
                    remote.send(trimmed);
                }
            }
        }
    }

    public Engine getAgentEntity() {
        return agentEntity;
    }

    @Override
    public synchronized boolean assignGame(LiveGame game) throws InterruptedException {
        if (this.liveGame != null) {
            if(Objects.equals(this.liveGame.getGame().getId(), game.getGame().getId())) {
                // TODO: we should not allow this to happen if two sepparate configs are used for same agent
                // switching config between moves is either unsupported or slow.
                logger.info("agent {}: joining again game {}", this.agentEntity.getId(), game.getGame().getId());
                return true;
            } else {
                logger.info("agent {}: rejecting {}, already busy with game {}", this.agentEntity.getId(), game.getGame().getId(), this.liveGame.getGame().getId());
                return false;
            }
        }
        logger.info("agent {}: joining game {}", this.agentEntity.getId(), game.getGame().getId());
        this.liveGame = game;
        return true;
    }

    @Override
    public synchronized void release(LiveGame game) {
        if (this.liveGame == game) {
            logger.info("agent {}: leaving game {}", this.agentEntity.getId(), liveGame.getGame().getId());
            this.liveGame = null;
        }
    }

    @Override
    public int getId() {
        return agentEntity.getId();
    }

    @Override
    public synchronized Optional<LiveGame> getLiveGame() {
        return Optional.ofNullable(this.liveGame);
    }

    private static class CompletableFutures<T> {
        private final Queue<CompletableFuture<T>> futures = new ArrayDeque<>();

        private void add(CompletableFuture<T> readyFuture) {
            futures.add(readyFuture);
        }
        
        private CompletableFuture<T> addNew() {
            CompletableFuture<T> readyFuture = new CompletableFuture<>();
            futures.add(readyFuture);
            return readyFuture;
        }
        
        private void complete(T result) {
            while(true) {
                CompletableFuture<T> future = futures.poll();
                if (future == null) {
                    break;
                }
                future.complete(result);
            }
        }
        
    }
    
    enum State {
        WAIT_FOR_UCI_OK,
        WAIT_FOR_READY_OK,
        WAIT_FOR_MOVE;
    }
}
