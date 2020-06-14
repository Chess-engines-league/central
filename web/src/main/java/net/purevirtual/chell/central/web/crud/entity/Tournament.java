package net.purevirtual.chell.central.web.crud.entity;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import net.purevirtual.chell.central.web.crud.entity.dto.MatchConfig;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Tournament {
    private static final Logger logger = LoggerFactory.getLogger(Tournament.class);
    
    @Id
    @SequenceGenerator(name = "tournament_id_seq", sequenceName = "tournament_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournament_id_seq")
    @Column(updatable = false)
    private Integer id;
    
    @OneToMany(mappedBy = "tournament")
    private Set<Match> matched = new HashSet<>();
    
    @Enumerated(EnumType.STRING)
    private MatchState state;
    private String config;
    private int gameCount;
  
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    
}
