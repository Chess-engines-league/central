package net.purevirtual.chell.central.web.crud.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import net.purevirtual.chell.central.web.crud.entity.enums.EngineType;

@Entity
public class Engine {

    @Id
    @SequenceGenerator(name = "agent_id_seq", sequenceName = "agent_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_id_seq")
    @Column(updatable = false)
    private Integer id;
        
    private String token;
    private String name;
    @Enumerated(EnumType.STRING)
    private EngineType type;
    
    //@OrderColumn(name = "order")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "subengines")
    private List<EngineConfig> subEngines;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EngineType getType() {
        return type;
    }

    public void setType(EngineType type) {
        this.type = type;
    }

    public List<EngineConfig> getSubEngines() {
        if (subEngines == null) {
            subEngines = new ArrayList<>();
        }
        return subEngines;
    }

    

}
