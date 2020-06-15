package net.purevirtual.chell.central.web.crud.entity.dto;

import java.time.Duration;

public class MatchConfig {
    private long timePerMoveMs = 1000;
    private long timePerGameS = 60 * 60 * 24L;//24h

    public long getTimePerMoveMs() {
        return timePerMoveMs;
    }

    public void setTimePerMoveMs(long timePerMoveMs) {
        this.timePerMoveMs = timePerMoveMs;
    }

    public long getTimePerGameS() {
        return timePerGameS;
    }

    public void setTimePerGameS(long timePerGameS) {
        this.timePerGameS = timePerGameS;
    }

}
