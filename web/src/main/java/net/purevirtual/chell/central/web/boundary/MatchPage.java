package net.purevirtual.chell.central.web.boundary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.crud.control.GameManager;
import net.purevirtual.chell.central.web.crud.control.MatchManager;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

@Path("/matches")
@Produces(MediaType.TEXT_HTML)
public class MatchPage extends PageResource {

    
    @Inject
    private MatchManager matchManager;
    
    @Inject
    private GameManager gameManager;

    @GET
    @Path("/{matchId}")
    public String get(@PathParam("matchId") int matchId) {
        Match match = matchManager.get(matchId);
        List<Game> games = gameManager.findByMatch(match);
        Map<String, Object> context = new HashMap();
        context.put("match", match);
        context.put("games", games);
        return getTemplateEngine().process("matches/match", new Context(null, context));
    }

    
    @GET
    public String list() {
        List<Match> matches = matchManager.findAll();
        Map<String, Object> context = new HashMap();

        context.put("matches", matches);
        return getTemplateEngine().process("matches/list", new Context(null, context));
    }
    
}
