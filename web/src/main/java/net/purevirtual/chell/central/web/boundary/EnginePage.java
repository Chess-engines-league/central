package net.purevirtual.chell.central.web.boundary;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import net.purevirtual.chell.central.web.agent.control.LiveAgentsManager;
import net.purevirtual.chell.central.web.crud.control.EngineConfigManager;
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.Game;
import net.purevirtual.chell.central.web.crud.entity.Match;
import net.purevirtual.chell.central.web.crud.entity.SubEnginesRelation;
import net.purevirtual.chell.central.web.crud.entity.enums.EngineType;
import net.purevirtual.chell.central.web.crud.entity.enums.GamePhase;
import net.purevirtual.chell.central.web.crud.entity.enums.HybridType;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;

@Path("/engines")
@Produces(MediaType.TEXT_HTML)
public class EnginePage extends PageResource {
    private static final Logger logger = LoggerFactory.getLogger(EnginePage.class);
    
    @Inject
    private EngineManager agentManager;
    
    @Inject
    private LiveAgentsManager liveAgentsManager;
    
    @Inject
    private EngineManager engineManager;
    
    @Inject
    private EngineConfigManager engineConfigManager;
    
    
    
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
    @Path("/fragment/subEngine")
    public String fragmentSubEngine() {
        List<Engine> engines = engineManager.findAll();
        var context = newModel();
        context.put("engines", engines);
        return getTemplateEngine().process("engines/subEngineFragment", new Context(null, context));
    }
    
    @GET
    @Path("/newHybrid")
    public String newHybrid() {
        List<Engine> engines = engineManager.findAll();
        var context = newModel();
        context.put("engines", engines);
        return getTemplateEngine().process("engines/newHybrid", new Context(null, context));
    }
    
    @GET
    @Path("/new")
    public String newEngine() {
        var context = newModel();
        context.put("types", Stream.of(EngineType.values()).filter(t -> t != EngineType.HYBRID).collect(Collectors.toList()));
        return getTemplateEngine().process("engines/new", new Context(null, context));
    }
    
    
    @POST
    @Path("/newHybrid")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newHybridEngineSubmit(@FormParam("engineConfigIds") String engineConfigIds,
            @FormParam("name") String name,
            @FormParam("host") String host,
            @FormParam("enginePhases") String enginePhases,
            @FormParam("moveSelectionType") String moveSelectionType
            
    ) throws URISyntaxException {
        logger.info("newHybrid name:{}, host: {}, ids:{}",
                name, host, engineConfigIds);
        Engine engine = new Engine();
        engine.setName(name);
        engine.setHost(host);
        engine.setType(EngineType.HYBRID);
        engineManager.save(engine); 
        String[] phases = enginePhases.split(";");
        String[] ids = engineConfigIds.split(";");
        for(int i=0;i<ids.length;i++) {
            String engineConfigId = ids[i];
            logger.info("engineConfigId={}", engineConfigId);
            EngineConfig subEngine = engineConfigManager.get(Integer.parseInt(engineConfigId));
            if(moveSelectionType.equals("vote")) {
                engineConfigManager.save(new SubEnginesRelation(engine, subEngine));
            } else {
                engineConfigManager.save(new SubEnginesRelation(engine, subEngine, GamePhase.valueOf(phases[i])));
            }
        };
        if(moveSelectionType.equals("vote")) {
            Stream.of(HybridType.values()).filter(t -> t != HybridType.PHASE).forEachOrdered(type -> {
                EngineConfig eg = new EngineConfig();
                eg.setElo(1200);
                eg.setEngine(engine);
                eg.setInitOptions("{\"type\":\"" + type.name() + "\"}");
                eg.setDescription(type.name().toLowerCase());
                engineConfigManager.save(eg);
            });
        } else {
            EngineConfig eg = new EngineConfig();
            eg.setElo(1200);
            eg.setEngine(engine);
            eg.setDescription("default");
            eg.setInitOptions("{\"type\":\"+PHASE+\"}");
            engineConfigManager.save(eg);
        }
        // relative to /gui
        return Response.temporaryRedirect(new URI("/engines/" + engine.getId())).build();
    }
    
    @POST
    @Path("/new")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newEngineSubmit(
            @FormParam("name") String name,
            @FormParam("host") String host,
            @FormParam("type") EngineType type
    ) throws URISyntaxException {
        logger.info("new name:{}, host: {}, type:{}",name, host, type);
        Engine engine = new Engine();
        engine.setName(name);
        engine.setHost(host);
        engine.setType(type);
        engine.setToken(RandomStringUtils.randomAlphabetic(20));
        engineManager.save(engine);
        
        
        EngineConfig ec = new EngineConfig();
        ec.setElo(1200);
        ec.setEngine(engine);
//        ec.setInitOptions("{\"type\":\"" + type.name() + "\"}");
        ec.setDescription("default");
        engineConfigManager.save(ec);      
        // relative to /gui
        return Response.temporaryRedirect(new URI("/engines/" + engine.getId())).build();
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
