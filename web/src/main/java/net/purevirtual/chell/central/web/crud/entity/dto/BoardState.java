package net.purevirtual.chell.central.web.crud.entity.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BoardState {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    List<BoardMove> boardMoves = new ArrayList<>();

    public List<BoardMove> getBoardMoves() {
        return boardMoves;
    }

    public void setBoardMoves(List<BoardMove> boardMoves) {
        this.boardMoves = boardMoves;
    }

    
    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static BoardState fromJson(String input) {
        if (input == null || input.isBlank()) {
            return new BoardState();
        }
        try {

            return MAPPER.readValue(input, BoardState.class);
        } catch (Exception ex) {
            // TODO: remove after conversion of older games
            BoardState tmp = new BoardState();
            Stream.of(input.split(" ")).forEach(m -> {
                BoardMove bm = new BoardMove();
                bm.setMove(m);
                tmp.getBoardMoves().add(bm);
            });
            return tmp;
        }
    }
    
    
    
}
