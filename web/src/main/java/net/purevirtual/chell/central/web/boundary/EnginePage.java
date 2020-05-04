package net.purevirtual.chell.central.web.boundary;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.purevirtual.chell.central.web.agent.control.LiveAgentsManager;
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.enums.EngineType;

@Path("/engines")
@Produces(MediaType.TEXT_HTML)
public class EnginePage extends PageResource {

    
    @Inject
    private EngineManager agentManager;
    
    @Inject
    private LiveAgentsManager liveAgentsManager;
    
    
    
    @GET
    @Path("/{engineId}")
    public String get(@PathParam("engineId") int engineId) {
        Engine engine = agentManager.get(engineId);
        String connectionCommand = null;
        if(engine.getType()!= EngineType.HYBRID) {
            String cmd;
            switch (engine.getType()) {
                case STOCKFISH:
                    cmd = "stockfish";
                    break;
                case LC0:
                    cmd = "./lc0";
                    break;
                default:
                    cmd = "<engineExecutableName>";
            }
            connectionCommand = "websocat -v --text -S cmd:'"+cmd+"' wss://chell.purevirtual.net:443/agent/uci/" + engine.getToken();
        }
        
        var context = newModel();
        context.put("engine", engine);
        context.put("connectionCommand", connectionCommand);
        if(engine.getType()==EngineType.HYBRID) {
            //context.put("hybridConfig", engine.get);
        }
        return process("engines/engine", context);
    }

    
    @GET
    public String list() {
        List<EngineDto> engines = agentManager.findAll().stream().map(EngineDto::new).collect(Collectors.toList());
        var context = newModel();
        context.put("engines", engines);
        return process("engines/list", context);
    }
    
    private class EngineDto {
        public String name;
        public EngineType type;
        public int id;
        public String lastConnected;
        public String host;
        public boolean online;
        public Game game = null;
        public Match match = null;
        public EngineDto(Engine engine) {
            lastConnected = "";
            if (engine.getLastConnected()!= null) {
                lastConnected = engine.getLastConnected().format(DateTimeFormatter.ISO_DATE_TIME);
            }
            name = engine.getName();
            id = engine.getId();
            type = engine.getType();
            host = engine.getHost();
            liveAgentsManager.find(engine).ifPresentOrElse(agent -> {
                this.online = true;
                agent.getLiveGame().map(t -> t.getGame()).
                        ifPresent(gm -> {
                            this.game = gm;
                            this.match = gm.getMatch();
                        });

            }, () -> online = false);
        }
    }
}
