package net.purevirtual.chell.central.web.crud.entity.dto;

import java.time.Duration;

public class MatchConfig {
    private long timePerMoveMs = 1000;
    private long timePerGameMs = 1000 * 60 * 60 * 24;//24h

    public long getTimePerMoveMs() {
        return timePerMoveMs;
    }

    public void setTimePerMoveMs(long timePerMoveMs) {
        this.timePerMoveMs = timePerMoveMs;
    }

    public long getTimePerGameMs() {
        return timePerGameMs;
    }

    public void setTimePerGameMs(long timePerGameMs) {
        this.timePerGameMs = timePerGameMs;
    }

}
