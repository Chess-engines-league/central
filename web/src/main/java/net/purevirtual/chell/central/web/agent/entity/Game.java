package net.purevirtual.chell.central.web.agent.entity;

import java.util.ArrayList;
import java.util.List;
import net.purevirtual.chell.central.web.agent.control.UciAgent;

public class Game {

    private List<String> moves = new ArrayList<>();
    private UciAgent white;
    private UciAgent black;
    private String result;

    public Game(UciAgent white, UciAgent black) {
        this.white = white;
        this.black = black;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

}
