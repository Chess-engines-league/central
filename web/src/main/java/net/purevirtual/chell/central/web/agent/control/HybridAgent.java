package net.purevirtual.chell.central.web.agent.control;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardMove;
import net.purevirtual.chell.central.web.crud.entity.enums.GamePhase;
import net.purevirtual.chell.central.web.crud.entity.enums.HybridType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HybridAgent implements IAgent {
    private static final Logger logger = LoggerFactory.getLogger(HybridAgent.class);
    
    private List<HybridSubAgent> subagents;
    private Engine engine;
    private LiveGame liveGame = null;
    private EngineConfig engineConfig = null;
    public HybridAgent(Engine engine, List<HybridSubAgent> subagents) {
        if(subagents.isEmpty()) {
            throw new IllegalArgumentException("Hybrid agent requires subAgents, invalid config for engine "+engine.getId());
        }
        this.engine = engine;
        this.subagents = subagents;
    }
    
    public static class HybridSubAgent {
        public IAgent agent;
        public EngineConfig engineConfig;
        public GamePhase gamePhase;

        public HybridSubAgent(IAgent agent, EngineConfig engineConfig, GamePhase gamePhase) {
            this.agent = agent;
            this.engineConfig = engineConfig;
            this.gamePhase = gamePhase;
        }

        @Override
        public String toString() {
            return "HybridSubAgent{"
                    + "agent=" + agent
                    + ", engineConfig=" + engineConfig
                    + ", gamePhase=" + gamePhase
                    + '}';
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
            liveGame = game;
            return true;
        } else {
            release(game);
            return false;
        }
    }
    
    @Override
    public synchronized Optional<LiveGame> getLiveGame() {
        return Optional.ofNullable(liveGame);
    }
    
    @Override
    public synchronized void release(LiveGame game) {
        for (HybridSubAgent subagent : subagents) {
            subagent.agent.release(game);
        }
        liveGame = null;
    }

    @Override
    public CompletableFuture<BoardMove> move(List<String> movesSoFar, long moveTimeLimit, Duration whiteClockLeft, Duration blackClockLeft) {
        final HybridType selectType;
        if (engineConfig != null) {
            selectType = engineConfig.getHybridConfig().getType();
        } else {
            selectType = HybridType.VOTE_ELO;
        }
        if (selectType == HybridType.PHASE) {
            return movePhase(movesSoFar, moveTimeLimit, whiteClockLeft, blackClockLeft);
        }
        List<CompletableFuture<BoardMove>> futures = new ArrayList<>();
        for (HybridSubAgent subagent : subagents) {
            futures.add(subagent.agent.move(movesSoFar, moveTimeLimit, whiteClockLeft, blackClockLeft));
        }
        return CompletableFuture.supplyAsync(()-> {
            CompletableFuture.allOf(toArray(futures));
            try {
                List<BoardMove> moves = new ArrayList<>();
                for (CompletableFuture<BoardMove> future : futures) {
                    moves.add(future.get());
                }
                
                BoardMove selectMove = HybridSelect.selectMove(selectType, moves, subagents);
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
    
    private CompletableFuture<BoardMove> movePhase(List<String> movesSoFar, long moveTimeLimit, Duration whiteClockLeft, Duration blackClockLeft) {
        GamePhase phase;
        if (movesSoFar.size() < 20) {
            phase = GamePhase.OPENING;
        } else {
            //endgame estimate like in https://www.chessstrategyonline.com/content/tutorials/basic-chess-concepts-phases-of-the-game
            Board board = new Board();
            if (!movesSoFar.isEmpty()) {

                MoveList list = new MoveList();
                try {
                    list.loadFromText(String.join(" ", movesSoFar));
                } catch (MoveConversionException ex) {
                    throw new RuntimeException(ex);
                }
                String fen = list.getFen();
                board.loadFromFen(fen);
            }
            long figCountWhite = countPieces(board, Piece.WHITE_BISHOP, Piece.WHITE_KNIGHT, Piece.WHITE_ROOK, Piece.WHITE_QUEEN);
            long figCountBlack = countPieces(board, Piece.BLACK_BISHOP, Piece.BLACK_KNIGHT, Piece.BLACK_ROOK, Piece.BLACK_QUEEN);
            if (figCountBlack <= 2 || figCountWhite <= 2) {
                phase = GamePhase.ENDGAME;
            } else {
                phase = GamePhase.MIDDLEGAME;
            }
            logger.info("selecting {}, figureCountWhite={}, figureCountBlack={}", phase, figCountWhite, figCountBlack);
        }
        logger.info("selected phase = {}, moves so far",phase,movesSoFar);
        CompletableFuture<BoardMove> move = getByPhase(phase).agent.move(movesSoFar, moveTimeLimit, whiteClockLeft, blackClockLeft);
        return CompletableFuture.supplyAsync(() -> {
            try {
                BoardMove val = move.get();
                val.appendComment("subengine selected by game phase " + phase);
                return val;
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            }
        });
    }
    
    private long countPieces(Board board, Piece... pieces) {
        int count = 0;

        for (Piece piece : pieces) {
            count += Long.bitCount(board.getBitboard(piece));
        }
        return count;
    }

    private HybridSubAgent getByPhase(GamePhase phase) {
        return subagents.stream().filter(t -> t.gamePhase == phase)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Missing subEngine for phase" + phase));
    }

    
    @Override
    public CompletableFuture<Void> reset(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
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
