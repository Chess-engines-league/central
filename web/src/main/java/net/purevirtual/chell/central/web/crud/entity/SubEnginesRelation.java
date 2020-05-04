package net.purevirtual.chell.central.web.crud.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "subengines")
public class SubEnginesRelation {

    @Id
    @SequenceGenerator(name = "subengines_id_seq", sequenceName = "subengines_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subengines_id_seq")
    @Column(updatable = false)
    private Integer id;
    
    @Column(name = "engine_id")
    private Integer engineId;

    @Column(name = "subengines_id")
    private Integer subEngineId;

    public SubEnginesRelation(Engine hybridEngine, EngineConfig subEngine) {
        engineId = hybridEngine.getId();
        subEngineId = subEngine.getId();

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEngineId() {
        return engineId;
    }

    public void setEngineId(Integer engineId) {
        this.engineId = engineId;
    }

    public Integer getSubEngineId() {
        return subEngineId;
    }

    public void setSubEngineId(Integer subEngineId) {
        this.subEngineId = subEngineId;
    }

}
