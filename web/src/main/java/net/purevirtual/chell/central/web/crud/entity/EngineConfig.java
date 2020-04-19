package net.purevirtual.chell.central.web.crud.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;

@Entity
public class EngineConfig {

    @Id
    @SequenceGenerator(name = "engineconfig_id_seq", sequenceName = "engineconfig_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "engineconfig_id_seq")
    @Column(updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="idagent")
    private Agent agent;
    private String description;
    private String initOptions;
    private int elo;
    
    @OrderColumn(name = "order")
    @ManyToMany
    @JoinTable(name = "subengines")
    private List<EngineConfig> subEngines;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInitOptions() {
        return initOptions;
    }

    public void setInitOptions(String initOptions) {
        this.initOptions = initOptions;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public List<EngineConfig> getSubEngines() {
        return subEngines;
    }

    public void setSubEngines(List<EngineConfig> subEngines) {
        this.subEngines = subEngines;
    }

}
