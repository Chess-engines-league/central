package net.purevirtual.chell.central.web.boundary;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import net.purevirtual.chell.central.web.agent.control.MatchMaker;
import net.purevirtual.chell.central.web.crud.control.EngineConfigManager;
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import net.purevirtual.chell.central.web.crud.control.TournamentManager;
import net.purevirtual.chell.central.web.crud.control.TournamentParticipantManager;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Tournament;
import net.purevirtual.chell.central.web.crud.entity.TournamentParticipant;
import net.purevirtual.chell.central.web.crud.entity.dto.MatchConfig;
import net.purevirtual.chell.central.web.crud.entity.enums.MatchState;

@Named
@ViewScoped
public class TournamentBean implements Serializable {
    
    private static final int DEAULT_ELO = 1200;

    @Inject
    private EngineManager engineManager;
    @Inject
    private EngineConfigManager engineConfigManager;
    @Inject
    private TournamentManager tournamentManager;
    @Inject
    private TournamentParticipantManager tournamentParticipantManager;
    
    @Inject
    private MatchMaker matchMaker;
    
    private List<Player> players = new ArrayList<>();
    private int gameCount = 8;
    private MatchConfig matchConfig = new MatchConfig();

    @PostConstruct
    public void init() {
        addPlayer();
        addPlayer();
    }

    public final void addPlayer() {
        Player player = new Player();
        Map<String, Integer> engines = getEngines();
        if (!engines.isEmpty()) {
            player.setEngineId(engines.values().iterator().next());
        }
        players.add(player);
    }
    
    public Map<String, Integer> getEngineConfigs(int engineId) {
        return engineConfigManager.findByEngine(engineId)
                .stream()
                .collect(Collectors.toMap(t -> t.getDescription() + " (" + t.getId() + ")", t -> t.getId()));
    }

    public Map<String, Integer> getEngines() {
        return engineManager.findAll()
                .stream()
                .collect(Collectors.toMap(t -> t.getName()+" ("+t.getId()+")", t -> t.getId()));
    }

    public List<Player> getPlayers() {
        return players;
    }
    
    public void createNew() throws IOException {
        Tournament tournament = new Tournament();
        tournament.setGameCount(gameCount);
        tournament.setConfig(matchConfig);
        tournament.setState(MatchState.PENDING);
        tournamentManager.save(tournament);
        List<EngineConfig> allConfigs = players.stream()
                .flatMap(t->t.engineConfigIds.stream())
                .distinct()
                .map(t->engineConfigManager.get(t))
                .collect(Collectors.toList());
        for (EngineConfig engineConfig : allConfigs) {
            TournamentParticipant participant = new TournamentParticipant();
            participant.setPlayer(engineConfig);
            participant.setTournament(tournament);
            participant.setElo(DEAULT_ELO);
            tournamentParticipantManager.save(participant);
        }
        for (EngineConfig player1 : allConfigs) {
            for (EngineConfig player2 : allConfigs) {
                if (player1.getId() >= player2.getId()) {
                    continue;
                }
                if (player1.getEngine().getId().equals(player2.getEngine().getId())) {
                    continue;
                }
                matchMaker.newMatch(player1, player2, gameCount, matchConfig, tournament);
            }
        }
        
        FacesContext.getCurrentInstance().getExternalContext().redirect("/gui/tournaments/"+tournament.getId());
        
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    public MatchConfig getMatchConfig() {
        return matchConfig;
    }

    public void setMatchConfig(MatchConfig matchConfig) {
        this.matchConfig = matchConfig;
    }

    public static class Player {

        private Integer engineId;
        private List<Integer> engineConfigIds;

        public Integer getEngineId() {
            return engineId;
        }

        public void setEngineId(Integer engineId) {
            this.engineId = engineId;
        }

        public List<Integer> getEngineConfigIds() {
            return engineConfigIds;
        }

        public void setEngineConfigIds(List<Integer> engineConfigIds) {
            this.engineConfigIds = engineConfigIds;
        }

    }
}
