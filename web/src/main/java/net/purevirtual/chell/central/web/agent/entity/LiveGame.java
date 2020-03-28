package net.purevirtual.chell.central.web.agent.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.purevirtual.chell.central.web.agent.control.UciAgent;
import net.purevirtual.chell.central.web.crud.entity.Game;

public class LiveGame {

    private List<String> moves = new ArrayList<>();
    private UciAgent white;
    private UciAgent black;
    private Game game;

    public LiveGame(Game game, UciAgent white, UciAgent black, String moves) {
        this.white = white;
        this.black = black;
        this.moves = Stream.of(moves.split("\\s+")).filter(s -> !s.isBlank()).collect(Collectors.toList());
        this.game = game;
    }
    
    public LiveGame(Game game, UciAgent white, UciAgent black) {
        this(game, white, black, game.getBoardState());
    }

    public List<String> getMoves() {
        return moves;
    }
    
    public String getMovesString() {
        return String.join(" ", moves);
    }

    public UciAgent getWhite() {
        return white;
    }

    public UciAgent getBlack() {
        return black;
    }

    public Game getGame() {
        return game;
    }

}
