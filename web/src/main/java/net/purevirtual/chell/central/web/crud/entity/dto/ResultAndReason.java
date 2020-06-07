package net.purevirtual.chell.central.web.crud.entity.dto;

import java.io.Serializable;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResultReason;

public class ResultAndReason implements Serializable {

    private final GameResult result;
    private final GameResultReason reason;

    public ResultAndReason(GameResult result, GameResultReason reason) {
        this.result = result;
        this.reason = reason;
    }

    public GameResult getResult() {
        return result;
    }

    public GameResultReason getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "{"
                + "result=" + result
                + ", reason=" + reason
                + '}';
    }
    
    public static ResultAndReason pending() {
        return new ResultAndReason(GameResult.PENDING, null);
    }
        
    public static ResultAndReason error() {
        return new ResultAndReason(GameResult.ERROR, null);
    }

}
