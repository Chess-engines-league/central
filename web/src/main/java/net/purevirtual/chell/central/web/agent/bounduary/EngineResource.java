package net.purevirtual.chell.central.web.agent.bounduary;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.agent.control.LiveAgentsManager;
import net.purevirtual.chell.central.web.crud.control.EngineConfigManager;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;

@Produces(value = MediaType.APPLICATION_JSON)
@Path("engines")
public class EngineResource {

    @Inject
    private LiveAgentsManager agentsManager;
    
    @Inject
    private EngineConfigManager engineConfigManager;

    @GET
    @Path("/{engineId}/configs")
    public List<SimpleEngineConfig> get(@PathParam("engineId") int engineId) {
        return engineConfigManager.findByEngine(engineId).stream().map(SimpleEngineConfig::new).collect(Collectors.toList());
    }
    
    private static class SimpleEngineConfig {
        public int id;
        public String name;

        public SimpleEngineConfig(EngineConfig ec) {
            this.id = ec.getId();
            this.name = ec.getDescription();
        }
        
    }
}
