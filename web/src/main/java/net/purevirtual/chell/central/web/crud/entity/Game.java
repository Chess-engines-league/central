package net.purevirtual.chell.central.web.crud.entity;

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

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }
    
    
}
