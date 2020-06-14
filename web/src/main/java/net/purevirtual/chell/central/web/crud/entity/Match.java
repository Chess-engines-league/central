package net.purevirtual.chell.central.web.crud.entity;

import com.google.gson.Gson;
import java.util.ArrayList;
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
import net.purevirtual.chell.central.web.crud.entity.dto.MatchConfig;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Match {
    private static final Logger logger = LoggerFactory.getLogger(Match.class);
    
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
    
    @ManyToOne
    @JoinColumn(name = "idtournament")
    private Tournament tournament;
    
    @Enumerated(EnumType.STRING)
    private MatchState state;
    private String result;
    private String config;
    private int gameCount;
    private int score1;
    private int score2;

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
    
    public MatchConfig getConfig() {
        if(config==null || config.isBlank()) {
            return new MatchConfig();
        }
        return new Gson().fromJson(config, MatchConfig.class);
    }

    public void setConfig(MatchConfig config) {
        this.config = new Gson().toJson(config);
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
        if (games == null) {
            games = new ArrayList<>();
        }
        return games;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
}
