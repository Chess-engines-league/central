package net.purevirtual.chell.central.web.crud.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
    private String host;
    private LocalDateTime lastConnected;
    @Enumerated(EnumType.STRING)
    private EngineType type;

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "engine", fetch = FetchType.EAGER)
    private Set<EngineConfig> configs;

    //@OrderColumn(name = "order")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "subengines")
    private Set<EngineConfig> subEngines;

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
            subEngines = new HashSet<>();
        }
        ArrayList<EngineConfig> tmp = new ArrayList<>(subEngines);
        Collections.sort(tmp, (a, b) -> a.getId().compareTo(b.getId()));
        return tmp;
    }

    public Set<EngineConfig> getConfigs() {
        return configs;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public LocalDateTime getLastConnected() {
        return lastConnected;
    }

    public void setLastConnected(LocalDateTime lastConnected) {
        this.lastConnected = lastConnected;
    }


}
