package net.purevirtual.chell.central.web.crud.entity.dto;

import java.util.HashMap;
import java.util.Map;

public class UciEngineOptions {
    private Map<String, String> options =new HashMap<>();
    private boolean ponder = false;
    public Map<String, String> getOptions() {
        return options;
    }

    public boolean isPonder() {
        return ponder;
    }

    public void setPonder(boolean ponder) {
        this.ponder = ponder;
    }

    @Override
    public String toString() {
        return "UciEngineOptions{" 
                + "options=" + options 
                + ", ponder=" + ponder 
                + '}';
    }
    

}
