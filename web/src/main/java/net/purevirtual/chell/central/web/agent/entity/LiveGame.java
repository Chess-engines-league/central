package net.purevirtual.chell.central.web.agent.entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.purevirtual.chell.central.web.agent.control.IAgent;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardMove;

public class LiveGame {

    private List<BoardMove> moves = new ArrayList<>();
    private IAgent white;
    private final EngineConfig whiteConfig;
    private IAgent black;
    private final EngineConfig blackConfig;

    private Game game;

    public LiveGame(Game game, IAgent white, IAgent black, EngineConfig whiteConfig, EngineConfig blackConfig, List<BoardMove> moves) {
        this.white = white;
        this.black = black;
        if (moves != null) {
            this.moves = moves;
        } else {
            this.moves = new ArrayList<>();
        }
        this.game = game;
        this.blackConfig = blackConfig;
        this.whiteConfig = whiteConfig;
        long timePerGameMs = game.getMatch().getConfig().getTimePerGameMs();
    }

    public LiveGame(Game game, IAgent white, IAgent black, EngineConfig whiteConfig, EngineConfig blackConfig) {
        this(game, white, black, whiteConfig, blackConfig, game.getBoardState().getBoardMoves());
    }

    public List<BoardMove> getMoves() {
        return moves;
    }

    public List<String> getRawMoves() {
        return moves.stream().map(BoardMove::getMove).collect(Collectors.toList());
    }

    public String getMovesString() {
        return String.join(" ", getRawMoves());
    }

    public IAgent getWhite() {
        return white;
    }

    public IAgent getBlack() {
        return black;
    }

    public Game getGame() {
        return game;
    }

    public EngineConfig getWhiteConfig() {
        return whiteConfig;
    }

    public EngineConfig getBlackConfig() {
        return blackConfig;
    }

    public Duration getWhiteClockLeft() {
        long timePerGameMs = game.getMatch().getConfig().getTimePerGameMs();
        return Duration.ofMillis(timePerGameMs - game.getClockWhite());
    }

    public Duration getBlackClockLeft() {
        long timePerGameMs = game.getMatch().getConfig().getTimePerGameMs();
        return Duration.ofMillis(timePerGameMs - game.getClockBlack());
    }

}
