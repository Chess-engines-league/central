package net.purevirtual.chell.central.web.crud.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import net.purevirtual.chell.central.web.crud.entity.dto.BoardState;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;

@Entity
public class Game {   
    @Id
    @SequenceGenerator(name="game_id_seq", sequenceName="game_id_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="user_id_seq")
    @Column(name = "id", updatable=false)
    private Integer id;
        
    private Integer gameNumber;

    @ManyToOne
    @JoinColumn(name = "idmatch")
    private Match match;
    
    private boolean whitePlayedByFirstAgent;
    
    private String boardState;
    @Enumerated(EnumType.STRING)
    private GameResult result;
    
    public List<String> getMoves() {
        if (boardState == null || boardState.isBlank()) {
            return new ArrayList<>();
        }
        return getBoardState().getBoardMoves().stream().map(bm->bm.getMove()).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public boolean isWhitePlayedByFirstAgent() {
        return whitePlayedByFirstAgent;
    }

    public void setWhitePlayedByFirstAgent(boolean whitePlayedByFirstAgent) {
        this.whitePlayedByFirstAgent = whitePlayedByFirstAgent;
    }

    public Integer getGameNumber() {
        return gameNumber;
    }

    public void setGameNumber(Integer gameNumber) {
        this.gameNumber = gameNumber;
    }

    public BoardState getBoardState() {
        return BoardState.fromJson(boardState);
    }

    public void setBoardState(BoardState boardState) {
        if(boardState!= null) {
            this.boardState = boardState.toJson();
        } else {
            this.boardState = "";
        }
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }
    
    
    public int getPlayer1Score() {
        if (result == GameResult.WHITE) {
            if (isWhitePlayedByFirstAgent()) {
                return 2;
            } else {
                return 0;
            }
        }
        if (result == GameResult.BLACK) {
            if (isWhitePlayedByFirstAgent()) {
                return 0;
            } else {
                return 2;
            }
        }
        //DRAW
        return 1;
    }
    
    public int getPlayer2Score() {
        return 2 - getPlayer1Score();
    }
    
}
