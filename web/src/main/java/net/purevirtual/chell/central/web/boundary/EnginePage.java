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
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.enums.EngineType;

@Path("/engines")
@Produces(MediaType.TEXT_HTML)
public class EnginePage extends PageResource {

    
    @Inject
    private EngineManager agentManager;
    
    
    
    @GET
    @Path("/{engineId}")
    public String get(@PathParam("engineId") int engineId) {
        Engine engine = agentManager.get(engineId);
        String connectionCommand = null;
        if(engine.getType()!= EngineType.HYBRID) {
            String cmd;
            switch (engine.getType()) {
                case STOCKFISH8:
                case STOCKFISH9:
                    cmd = "stockfish";
                    break;
                case LC0:
                    cmd = "./lc0";
                    break;
                default:
                    cmd = "<engineExecutableName>";
            }
            connectionCommand = "websocat -v --text -S cmd:'stockfish' wss://chell.purevirtual.net:443/agent/uci/" + engine.getToken();
        }
        Map<String, Object> context = new HashMap();
        context.put("engine", engine);
        context.put("connectionCommand", connectionCommand);
        return process("engines/engine", context);
    }

    
    @GET
    public String list() {
        List<Engine> agents = agentManager.findAll();
        Map<String, Object> context = new HashMap();
        context.put("engines", agents);
        return process("engines/list", context);
    }
    
}
