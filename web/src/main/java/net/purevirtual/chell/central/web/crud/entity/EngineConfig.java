package net.purevirtual.chell.central.web.crud.entity;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import net.purevirtual.chell.central.web.crud.entity.dto.HybridEngineOptions;

@Entity
public class EngineConfig {

    @Id
    @SequenceGenerator(name = "engineconfig_id_seq", sequenceName = "engineconfig_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "engineconfig_id_seq")
    @Column(updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="idengine")
    private Engine engine;
    private String description;
    private String initOptions;
    private int elo;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "subEngines")
    private Set<Engine> hybridEngines;

    public Set<Engine> getHybridEngines() {
        if (hybridEngines == null) {
            hybridEngines = new HashSet<>();
        }
        return hybridEngines;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
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
    
    public HybridEngineOptions getHybridConfig() {
        return new Gson().fromJson(getInitOptions(), HybridEngineOptions.class);
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    @Override
    public String toString() {
        return "EngineConfig{"
                + "id=" + id
                + ", engine=" + engine
                + ", description=" + description
                + '}';
    }

}
