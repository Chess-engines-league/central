package net.purevirtual.chell.central.web.crud.entity.enums;

// https://en.wikipedia.org/wiki/Draw_(chess)
public enum GameResultReason {
    MAT,
    INSUFFICIENT_MATERIAL,
    DRAW,
    STALEMATE,
    GAME_TIME_LIMIT,
    MOVE_TIME_LIMIT,
    ILLEGAL_MOVE,
    THREEFOLD_REPETITION,
    MAX_MOVES_LIMIT;
}
