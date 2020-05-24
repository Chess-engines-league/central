package net.purevirtual.chell.central.web.crud.entity.enums;

public enum Side {
    WHITE, BLACK;

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
