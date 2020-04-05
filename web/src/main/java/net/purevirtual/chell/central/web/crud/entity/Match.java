package net.purevirtual.chell.central.web.crud.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;

@Entity
public class Match {

    @Id
    @SequenceGenerator(name = "match_id_seq", sequenceName = "match_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_id_seq")
    @Column(updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idplayer1")
    private EngineConfig player1;

    @ManyToOne
    @JoinColumn(name = "idplayer2")
    private EngineConfig player2;
    
    @OneToMany(mappedBy = "match")
    @OrderBy("gameNumber ASC")
    private List<Game> games;
    
    @Enumerated(EnumType.STRING)
    private MatchState state;
    private String result;
    private int gameCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EngineConfig getPlayer1() {
        return player1;
    }

    public EngineConfig getPlayer2() {
        return player2;
    }

    public void setPlayer1(EngineConfig player1) {
        this.player1 = player1;
    }

    public void setPlayer2(EngineConfig player2) {
        this.player2 = player2;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    public List<Game> getGames() {
        return games;
    }

}
