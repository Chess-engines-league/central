package net.purevirtual.chell.central.web.agent.control;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UciAgent {
    private static final Logger logger = LoggerFactory.getLogger(UciAgent.class);
    private final AgentInput remote;
    private State state;
    // FIXME: maybe optional or normal variables instead of lists?
    private List<CompletableFuture<Void>> readyFutures = new ArrayList<>();
    private List<CompletableFuture<String>> moveFutures = new ArrayList<>();
    private LocalDateTime lastMessage = null;
    public UciAgent(AgentInput remote) {
        this.remote = remote;
        state = State.WAIT_FOR_UCI_OK;
        remote.send("uci");
    }
    
    public void heartbeat(int seconds) {
        if (lastMessage== null ||lastMessage.plusSeconds(seconds).isBefore(LocalDateTime.now())) {
            remote.heartbeat();
        }
    }
    
    public Future<String>  move(List<String> movesSoFar, long moveTimeLimit) {
        state=State.WAIT_FOR_MOVE;
        if (movesSoFar.isEmpty()) {
            remote.send("position startpos");
        } else {
            remote.send("position startpos moves " + String.join(" ", movesSoFar));
        }
        remote.send("go movetime "+moveTimeLimit);
        CompletableFuture<String> moveFuture = new CompletableFuture<>();
        moveFutures.add(moveFuture);
        return moveFuture;
    }

    public void onMessage(String message) {
        String[] parts = message.split("\\s+");
        if (parts.length == 0) {
            return;
        }
        String cmd = parts[0];
        switch (cmd) {
            case "bestmove":
                String move = parts[1];
                // TODO: make this configurable
                //remote.send("go ponder");
                moveFutures.forEach(f -> f.complete(move));
                moveFutures.clear();
                break;
            case "info":
                logger.debug(message);
                break;
            case "uciok":
                state = State.WAIT_FOR_READY_OK;
                remote.send("ucinewgame", "isready");

                break;
            case "readyok":
                readyFutures.forEach(f -> f.complete(null));
                readyFutures.clear();

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

    public Future<Void> reset() {
        state = State.WAIT_FOR_READY_OK;
        remote.send("ucinewgame", "isready");
        CompletableFuture<Void> readyFuture = new CompletableFuture<>();
        readyFutures.add(readyFuture);
        return readyFuture;
        
    }

    enum State {
        WAIT_FOR_UCI_OK,
        WAIT_FOR_READY_OK,
        WAIT_FOR_MOVE;
    }
}
