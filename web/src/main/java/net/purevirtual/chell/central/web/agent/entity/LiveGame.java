package net.purevirtual.chell.central.web.agent.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.purevirtual.chell.central.web.agent.control.IAgent;
import net.purevirtual.chell.central.web.agent.control.UciAgent;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;

public class LiveGame {

    private List<String> moves = new ArrayList<>();
    private IAgent white;
    private final EngineConfig whiteConfig;
    private IAgent black;
    private final EngineConfig blackConfig;

    private Game game;

    public LiveGame(Game game, IAgent white, IAgent black, EngineConfig whiteConfig, EngineConfig blackConfig, String moves) {
        this.white = white;
        this.black = black;
        if (moves != null) {
            this.moves = Stream.of(moves.split("\\s+")).filter(s -> !s.isBlank()).collect(Collectors.toList());
        } else {
            this.moves = new ArrayList<>();
        }
        this.game = game;
        this.blackConfig = blackConfig;
        this.whiteConfig = whiteConfig;
    }

    public LiveGame(Game game, IAgent white, IAgent black, EngineConfig whiteConfig, EngineConfig blackConfig) {
        this(game, white, black, whiteConfig, blackConfig, game.getBoardState());
    }

    public List<String> getMoves() {
        return moves;
    }

    public String getMovesString() {
        return String.join(" ", moves);
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

}
