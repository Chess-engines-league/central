package net.purevirtual.chell.central.web.crud.entity.dto;

import net.purevirtual.chell.central.web.crud.entity.enums.HybridType;

public class HybridEngineOptions {
    private HybridType type;

    public HybridType getType() {
        return type;
    }

    public void setType(HybridType type) {
        this.type = type;
    }
}
