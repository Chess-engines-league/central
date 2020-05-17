package net.purevirtual.chell.central.web.agent.control;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardMove;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardState;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class GameRunner {

    private static final Logger logger = LoggerFactory.getLogger(GameRunner.class);
    
    @Inject
    private GameManager gameManager;

    @Asynchronous
    public void run(LiveGame game) {
        runSync(game);
    }
    
    public GameResult runSync(LiveGame game) {
        IAgent white = game.getWhite();
        IAgent black = game.getBlack();
        logger.info("running {} between {} and {}", game, white.getId(), black.getId());
        try {
            
            if (!white.assignGame(game)) {
                logger.info("Cannot start game {} from match {}, the agent {} is busy",
                        game.getGame().getId(), game.getGame().getMatch().getId(), game.getWhite().getId()
                );
                return GameResult.PENDING;
            }
            if (!black.assignGame(game)) {
                logger.info("Cannot start game {} from match {}, the agent {} is busy",
                        game.getGame().getId(), game.getGame().getMatch().getId(), game.getBlack().getId()
                );
                return GameResult.PENDING;
            }
            GameResult result = runInternal(game);
            logger.info("game done, result {}", result);
            gameManager.updateGameResult(game.getGame(), result);
            return result;
        } catch (InterruptedException ex) {
            logger.error("Game execution was interrupted", ex);
            Thread.currentThread().interrupt();
            return GameResult.PENDING;
        } finally {
            white.release(game);
            black.release(game);
        }
    }

    private GameResult runInternal(LiveGame game) throws InterruptedException {
        try {
            logger.info("reseting agents");
            long timePerMoveMs = game.getGame().getMatch().getConfig().getTimePerMoveMs();
            Future<Void> resetW = game.getWhite().reset(game.getWhiteConfig());
            Future<Void> resetB = game.getBlack().reset(game.getBlackConfig());
            try {
                resetW.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                logger.error("Failed to start game, can't reset white agent {}", game.getWhite());
            }
            try {
                resetB.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                logger.error("Failed to start game, can't reset black agent {}", game.getWhite());
            }
            logger.info("agents resetted");
            for (int i = 1; i <= 2000; i++) {
                if (game.getMoves().size() % 2 == 0) {
                    // if the number of half moves is even, then WHITE should move
                    Optional<GameResult> result = halfMove(game, game.getWhite(), timePerMoveMs, Side.WHITE);
                    if (result.isPresent()) {
                        return result.get();
                    }
                } else {
                    Optional<GameResult> result = halfMove(game, game.getBlack(), timePerMoveMs, Side.BLACK);
                    if (result.isPresent()) {
                        return result.get();
                    }
                }
            }

        } catch (ExecutionException | MoveConversionException |MoveGeneratorException ex) {
            logger.error("Game failed to complete", ex);
            return GameResult.ERROR;
        }
        logger.error("Game failed to complete - move limit reached");
        return GameResult.ERROR;
    }
    
    private Optional<GameResult> halfMove(LiveGame game, IAgent agent, long moveLimit, Side side) throws InterruptedException, MoveConversionException, MoveGeneratorException {
        logger.info("{} start a move", side);
        Future<BoardMove> moveFuture = agent.move(game.getRawMoves(), moveLimit);
        Instant moveStart = Instant.now();
        BoardMove move;
        try {
            move = moveFuture.get(2 * moveLimit, TimeUnit.MILLISECONDS);
        } catch (TimeoutException|ExecutionException ex) {
            logger.info("{} failed to return move in allowed time", side);
            return Optional.of(side.opponent().result());
        }
        Duration moveDuration = Duration.between(moveStart, Instant.now());
        move.appendComment(String.format("received move in %d ms, limit: %d", moveDuration.toMillis(), moveLimit));
        if (!isLegal(game.getMovesString(), move.getMove())) {
            logger.info("{} illegal move:  {}", side, move);
            return Optional.of(side.opponent().result());
        }
        game.getMoves().add(move);
        final BoardState boardState = new BoardState();
        boardState.setBoardMoves(game.getMoves());
        gameManager.updateBoardState(game.getGame(), boardState);
        logger.info("all moves after {} move: {}", side, game.getRawMoves());
        return isDone(game.getMovesString(), side);
    }

    private Optional<GameResult> isDone(String len, Side side) throws MoveConversionException {
        MoveList list = new MoveList();
        list.loadFromText(len);
        String fen = list.getFen();
        Board board = new Board();
        board.loadFromFen(fen);
        logger.info("fen: {}", fen);
        
        logger.info("state\n{}", board);
        logger.info("draw={}, InsufficientMaterial={}, KingAttacked={}, mated={}, stalemate={}",
                board.isDraw(), board.isInsufficientMaterial(), board.isKingAttacked(), board.isMated(), board.isStaleMate());
        if (board.isDraw() || board.isStaleMate()) {
            return Optional.of(GameResult.DRAW);
        }
        if (board.isMated()) {
            logger.info("returning {}", side);
            return Optional.of(side.result());
        }
        return Optional.empty();
    }

    private boolean isLegal(String len, String move) throws MoveConversionException, MoveGeneratorException {
        
        Board board = new Board();
        if (!len.isEmpty()) {
            MoveList list = new MoveList();
            list.loadFromText(len);
            String fen = list.getFen();
            board.loadFromFen(fen);
        }
        return MoveGenerator.generateLegalMoves(board)
                .stream()
                .anyMatch(legal -> move.equals(legal.toString()));
    }
    
    enum Side {
        WHITE,
        BLACK;

        public Side opponent() {
            if (this == WHITE) {
                return BLACK;
            }
            return WHITE;
        }
        
        public GameResult result() {
            if (this == WHITE) {
                return GameResult.WHITE;
            }
            return GameResult.BLACK;
        }
    }

}
