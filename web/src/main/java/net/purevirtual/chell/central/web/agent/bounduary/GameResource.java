package net.purevirtual.chell.central.web.agent.bounduary;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.agent.control.LiveAgentsManager;
import net.purevirtual.chell.central.web.agent.control.GameRunner;
import net.purevirtual.chell.central.web.agent.control.MatchMaker;
import net.purevirtual.chell.central.web.agent.control.UciAgent;
import net.purevirtual.chell.central.web.agent.entity.LiveGame;

@Path("/games")
public class GameResource {

    @Inject
    private LiveAgentsManager agentsManager;
    
    @Inject
    private MatchMaker matchMaker;
    
    @Inject
    private MatchRunner matchRunner;
    
    @GET
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path("new/random")
    public String randomGame() {
        List<String> list = agentsManager.list();

        UciAgent whiteAgent = agentsManager.get(list.get(0));
        UciAgent blackAgent = agentsManager.get(list.get(1));
        matchMaker.newMatch(whiteAgent.getAgentEntity(), blackAgent.getAgentEntity(), 10);
        matchRunner.wake();
        return "started " + list.get(0) + " vs " + list.get(1);
    }
}
