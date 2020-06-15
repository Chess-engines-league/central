package net.purevirtual.chell.central.web.boundary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.agent.bounduary.MatchRunner;
import net.purevirtual.chell.central.web.agent.control.MatchMaker;
import net.purevirtual.chell.central.web.crud.control.EngineConfigManager;
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.control.TournamentManager;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.Tournament;
import net.purevirtual.chell.central.web.crud.entity.TournamentParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

@Path("/tournaments")
@Produces(MediaType.TEXT_HTML)
public class TournamentPage extends PageResource {
    private static final Logger logger = LoggerFactory.getLogger(TournamentPage.class);

    
    @Inject
    private MatchManager matchManager;
    
    @Inject
    private GameManager gameManager;
    
    @Inject
    private EngineManager engineManager;
    
    @Inject
    private EngineConfigManager engineConfigManager;
    
    @Inject
    private MatchMaker matchMaker;
    
    @Inject
    private MatchRunner matchRunner;
    
    @Inject
    private TournamentManager tournamentManager;

    @GET
    @Path("/{tournamentId}")
    public String get(@PathParam("tournamentId") int tournamentId) {
        Tournament tournament = tournamentManager.get(tournamentId);
        var context = newModel();
        context.put("tournament", tournament);
        List<Match> matches = matchManager.findByTournament(tournament);
        context.put("matches", matches);
        List<ParticipantDTO> participants = tournament.getParticipants().stream()
                .map(ParticipantDTO::new)
                .sorted(Comparator.comparing(ParticipantDTO::getElo).reversed())
                .collect(Collectors.toList());
        Map<Integer, ParticipantDTO> participantsByConfigId = participants
                        .stream()
                        .collect(Collectors.toMap(p->p.getPlayer().getId(), p->p));
        for (Match match : matches) {
                    ParticipantDTO participant1 = participantsByConfigId.get(match.getPlayer1().getId());
                    ParticipantDTO participant2 = participantsByConfigId.get(match.getPlayer2().getId());
            List<Game> games = gameManager.findByMatch(match);
            for (Game game : games) {
                logger.info("for game {} scores are {} and {}", game.getId(), game.getPlayer1Score(),game.getPlayer2Score());
                switch (game.getPlayer1Score()) {
                    case 0:
                        participant1.loses++;
                        break;
                    case 1:
                        participant1.draws++;
                        break;
                    case 2:
                        participant1.wins++;
                        break;
                }
                participant1.totalGames++;
                switch (game.getPlayer2Score()) {
                    case 0:
                        participant2.loses++;
                        break;
                    case 1:
                        participant2.draws++;
                        break;
                    case 2:
                        participant2.wins++;
                        break;
                }
                participant2.totalGames++;
            }
        }
        participants = participants.stream()
                .sorted(Comparator.comparing(ParticipantDTO::getElo).reversed().thenComparing(ParticipantDTO::getWins))
                .collect(Collectors.toList());
        context.put("participants", participants);
        //
        return getTemplateEngine().process("tournaments/tournament", new Context(null, context));
    }

    
    @GET
    public String list() {
        List<Tournament> tournaments = tournamentManager.findAll();
        var context = newModel();

        context.put("tournaments", tournaments);
        return getTemplateEngine().process("tournaments/list", new Context(null, context));
    }
   
    
    private static class ParticipantDTO {

        private final int elo;
        private final EngineConfig player;
        private int wins = 0;
        private int draws = 0;
        private int loses = 0;
        private int totalGames = 0;

        public ParticipantDTO(TournamentParticipant tp) {
            this.elo = tp.getElo();
            this.player = tp.getPlayer();
        }

        public int getElo() {
            return elo;
        }

        public EngineConfig getPlayer() {
            return player;
        }

        public int getWins() {
            return wins;
        }

        public int getDraws() {
            return draws;
        }

        public int getLoses() {
            return loses;
        }

        public int getTotalGames() {
            return totalGames;
        }
        
        
        
        
    }
    
}
