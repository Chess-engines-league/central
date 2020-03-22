package net.purevirtual.chell.central.web.agent.bounduary;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.agent.control.AgentsManager;
import net.purevirtual.chell.central.web.agent.control.GameRunner;
import net.purevirtual.chell.central.web.agent.control.UciAgent;
import net.purevirtual.chell.central.web.agent.entity.Game;

@Path("games")
public class GameResource {

    @Inject
    private AgentsManager agentsManager;
    
    @Inject
    private GameRunner gameRunner;

    @GET
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path("new/{white}/{black}")
    public String newGame(@PathParam("white") String white, @PathParam("black") String black) {
        UciAgent whiteAgent = agentsManager.get(white);
        UciAgent blackAgent = agentsManager.get(black);
        Game game = new Game(whiteAgent, blackAgent);
        gameRunner.run(game);
        return "started "+white+" vs "+black;
    }
    
    @GET
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path("new/random")
    public String randomGame() {
        List<String> list = agentsManager.list();
        
        UciAgent whiteAgent = agentsManager.get(list.get(0));
        UciAgent blackAgent = agentsManager.get(list.get(1));
        Game game = new Game(whiteAgent, blackAgent);
        gameRunner.run(game);
        return "started "+list.get(0)+" vs "+list.get(1);
    }
}
