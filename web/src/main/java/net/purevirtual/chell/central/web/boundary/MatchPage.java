package net.purevirtual.chell.central.web.boundary;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.purevirtual.chell.central.web.agent.bounduary.MatchRunner;
import net.purevirtual.chell.central.web.agent.control.MatchMaker;
import net.purevirtual.chell.central.web.crud.control.EngineConfigManager;
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.enums.GameResult;
import org.thymeleaf.context.Context;

@Path("/matches")
@Produces(MediaType.TEXT_HTML)
public class MatchPage extends PageResource {

    
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

    @GET
    @Path("/{matchId}")
    public String get(@PathParam("matchId") int matchId) {
        Match match = matchManager.get(matchId);
        List<GameWithScore> games = gameManager.findByMatch(match).stream()
                .map(game -> {
                    String score1 = "";
                    String score2 = "";
                    if (game.getResult() == GameResult.WHITE) {
                        if (game.isWhitePlayedByFirstAgent()) {
                            score1 = "1";
                            score2 = "0";
                        } else {
                            score1 = "0";
                            score2 = "1";
                        }
                    }
                    if (game.getResult() == GameResult.BLACK) {
                        if (game.isWhitePlayedByFirstAgent()) {
                            score1 = "0";
                            score2 = "1";
                        } else {
                            score1 = "1";
                            score2 = "0";
                        }
                    }
                    if (game.getResult() == GameResult.DRAW) {
                        score1 = "0.5";
                        score2 = "0.5";
                    }
                    return new GameWithScore(game, score1, score2);
                }).collect(Collectors.toList());
        var context = newModel();
        context.put("match", match);
        context.put("player1", match.getPlayer1());
        context.put("player2", match.getPlayer2());
        context.put("games", games);
        return getTemplateEngine().process("matches/match", new Context(null, context));
    }
    
    @GET
    @Path("/new")
    public String newMatch() {
        List<Engine> engines = engineManager.findAll();
        var context = newModel();
        context.put("engines", engines);
        return getTemplateEngine().process("matches/new", new Context(null, context));
    }
    
    
    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newMatchSubmit(
            @FormParam("engineConfig1") int engineConfig1,
            @FormParam("engineConfig2") int engineConfig2,
            @FormParam("games") int games) throws URISyntaxException {
        EngineConfig engine1 = engineConfigManager.get(engineConfig1);
        EngineConfig engine2 = engineConfigManager.get(engineConfig2);
        Match match = matchMaker.newMatch(engine1, engine2, games);
        matchRunner.wake();
        // relative to /gui
        return Response.temporaryRedirect(new URI("/matches/" + match.getId())).build();
    }

    
    @GET
    public String list() {
        List<Match> matches = matchManager.findAll();
        var context = newModel();

        context.put("matches", matches);
        return getTemplateEngine().process("matches/list", new Context(null, context));
    }
    
    private static class GameWithScore {
        public Game game;
        public String score1;
        public String score2;

        public GameWithScore(Game game, String score1, String score2) {
            this.game = game;
            this.score1 = score1;
            this.score2 = score2;
        }
    }
    
}
