package net.purevirtual.chell.central.web.crud.entity;

import com.google.gson.Gson;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import net.purevirtual.chell.central.web.crud.entity.dto.HybridEngineOptions;
import net.purevirtual.chell.central.web.crud.entity.dto.UciEngineOptions;

@Entity
public class EngineConfig implements Serializable {
    private static final Gson GSON = new Gson();
    
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
        
    public UciEngineOptions getUciConfig() {
        if(initOptions==null || initOptions.isBlank()) {
            return new UciEngineOptions();
        }
        return new Gson().fromJson(initOptions, UciEngineOptions.class);
    }
    
    public HybridEngineOptions getHybridConfig() {
        if (initOptions == null || initOptions.isBlank()) {
            return new HybridEngineOptions();
        }
        return new Gson().fromJson(initOptions, HybridEngineOptions.class);
    }
    
    public void setHybridConfig(HybridEngineOptions config) {
        initOptions = GSON.toJson(config);
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
