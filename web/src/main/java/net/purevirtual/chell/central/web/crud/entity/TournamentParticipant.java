package net.purevirtual.chell.central.web.crud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class TournamentParticipant {
    private static final Logger logger = LoggerFactory.getLogger(TournamentParticipant.class);
    
    @Id
    @SequenceGenerator(name = "tournamentparticipant_id_seq", sequenceName = "tournamentparticipant_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tournamentparticipant_id_seq")
    @Column(updatable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idplayer")
    private EngineConfig player;
       
    @ManyToOne
    @JoinColumn(name = "idtournament")
    private Tournament tournament;
    
    private int elo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EngineConfig getPlayer() {
        return player;
    }

    public void setPlayer(EngineConfig player) {
        this.player = player;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    
}
