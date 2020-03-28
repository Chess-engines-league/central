package net.purevirtual.chell.central.web.agent.bounduary;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.agent.control.LiveAgentsManager;

@Path("agents")
public class AgentResource {

    @Inject
    private LiveAgentsManager agentsManager;

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public List<String> list() {
        return agentsManager.list();
    }
}
