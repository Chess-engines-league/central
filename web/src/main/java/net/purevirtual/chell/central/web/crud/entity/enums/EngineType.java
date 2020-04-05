package net.purevirtual.chell.central.web.crud.entity.enums;

public enum EngineType {
    ETHEREAL12("Ethereal 12"),
    FRUIT("Fruit"),
    GLAURUNG("Glaurung"),
    LC0("Leela Chess Zero"),
    OTHER("Other"),
    STOCKFISH8("Stockfish 8"),
    STOCKFISH9("Stockfish 9"),
    TOGA2("Toga II");

    private final String name;

    EngineType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
