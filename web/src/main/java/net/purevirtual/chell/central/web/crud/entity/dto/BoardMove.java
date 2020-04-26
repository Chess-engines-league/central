package net.purevirtual.chell.central.web.crud.entity.dto;

import java.io.Serializable;

public class BoardMove implements Serializable {
    /** like "e2e4" */
    String move;
    /** expected opponent move */
    String ponder;
    /** player/engine comment */
    String comment;

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getPonder() {
        return ponder;
    }

    public void setPonder(String ponder) {
        this.ponder = ponder;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    
}
