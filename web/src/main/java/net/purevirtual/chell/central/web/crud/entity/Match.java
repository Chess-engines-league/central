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
    @JoinColumn(name = "idagent1")
    private Agent agent1;

    @ManyToOne
    @JoinColumn(name = "idagent2")
    private Agent agent2;
    
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

    public Agent getAgent1() {
        return agent1;
    }

    public void setAgent1(Agent agent1) {
        this.agent1 = agent1;
    }

    public Agent getAgent2() {
        return agent2;
    }

    public void setAgent2(Agent agent2) {
        this.agent2 = agent2;
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
