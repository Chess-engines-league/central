package net.purevirtual.chell.central.web.crud.entity.dto;

public class MatchConfig {
    private long timePerMoveMs = 1000;

    public long getTimePerMoveMs() {
        return timePerMoveMs;
    }

    public void setTimePerMoveMs(long timePerMoveMs) {
        this.timePerMoveMs = timePerMoveMs;
    }

}
