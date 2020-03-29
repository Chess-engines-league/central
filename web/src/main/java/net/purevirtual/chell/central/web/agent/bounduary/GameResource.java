package net.purevirtual.chell.central.web.agent.bounduary;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.agent.control.LiveAgentsManager;
import net.purevirtual.chell.central.web.agent.control.MatchMaker;
import net.purevirtual.chell.central.web.agent.control.UciAgent;
import net.purevirtual.chell.central.web.crud.control.AgentManager;
import net.purevirtual.chell.central.web.crud.entity.Agent;

@Path("/games")
public class GameResource {

    @Inject
    private LiveAgentsManager liveAgentsManager;
    
    @Inject
    private AgentManager agentManager;
    
    @Inject
    private MatchMaker matchMaker;
    
    @Inject
    private MatchRunner matchRunner;
    
    @GET
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path("new/random")
    public String randomGame() {
        List<String> list = liveAgentsManager.list();

        UciAgent whiteAgent = liveAgentsManager.get(list.get(0));
        UciAgent blackAgent = liveAgentsManager.get(list.get(1));
        matchMaker.newMatch(whiteAgent.getAgentEntity(), blackAgent.getAgentEntity(), 10);
        matchRunner.wake();
        return "started " + list.get(0) + " vs " + list.get(1);
    }
    
    @GET
    @Produces(value = MediaType.TEXT_PLAIN)
    @Path("new/{agent1}/{agent2}")
    public String newGame(@PathParam("agent1") int agent1, @PathParam("agent2") int agent2, @QueryParam("games") Integer games) {
        if (games == null) {
            games = 10;
        }
        Agent whiteAgent = agentManager.get(agent1);
        Agent blackAgent = agentManager.get(agent2);
        matchMaker.newMatch(whiteAgent, blackAgent, games);
        matchRunner.wake();
        return "started " + agent1 + " vs " + agent2;
    }
}
