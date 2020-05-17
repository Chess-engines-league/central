package net.purevirtual.chell.central.web.crud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import net.purevirtual.chell.central.web.crud.entity.enums.GamePhase;

@Entity
@Table(name = "subengines")
public class SubEnginesRelation {

    @Id
    @SequenceGenerator(name = "subengines_id_seq", sequenceName = "subengines_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subengines_id_seq")
    @Column(updatable = false)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "engine_id")
    private Engine engine;

    @ManyToOne
    @JoinColumn(name = "subengines_id")
    private EngineConfig subEngine;
    
    @Enumerated(EnumType.STRING)
    private GamePhase gamePhase;

    public SubEnginesRelation() {
    }
    
    public SubEnginesRelation(Engine hybridEngine, EngineConfig subEngine, GamePhase gamePhase) {
        this.engine = hybridEngine;
        this.subEngine = subEngine;
        this.gamePhase = gamePhase;
    }

    public SubEnginesRelation(Engine hybridEngine, EngineConfig subEngine) {
        this.engine = hybridEngine;
        this.subEngine = subEngine;
    }

    public EngineConfig getSubEngine() {
        return subEngine;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public Integer getEngineId() {
//        return engineId;
//    }
//
//    public void setEngineId(Integer engineId) {
//        this.engineId = engineId;
//    }
//
//    public Integer getSubEngineId() {
//        return subEngineId;
//    }
//
//    public void setSubEngineId(Integer subEngineId) {
//        this.subEngineId = subEngineId;
//    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
    }
}
