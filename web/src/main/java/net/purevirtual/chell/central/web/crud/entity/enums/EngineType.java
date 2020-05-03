package net.purevirtual.chell.central.web.crud.entity.enums;

public enum EngineType {
    ETHEREAL12("Ethereal 12"),
    FRUIT("Fruit"),
    GLAURUNG("Glaurung"),
    HYBRID("HYBRID"),
    LC0("Leela Chess Zero"),
    OTHER("Other"),
    STOCKFISH("Stockfish"),
    TOGA2("Toga II");

    private final String name;

    EngineType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
