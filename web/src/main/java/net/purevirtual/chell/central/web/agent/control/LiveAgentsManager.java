package net.purevirtual.chell.central.web.agent.control;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import net.purevirtual.chell.central.web.crud.entity.Engine;
import net.purevirtual.chell.central.web.crud.entity.EngineConfig;
import net.purevirtual.chell.central.web.crud.entity.SubEnginesRelation;
import net.purevirtual.chell.central.web.crud.entity.enums.EngineType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class LiveAgentsManager {
    private static final Logger logger = LoggerFactory.getLogger(LiveAgentsManager.class);
    
    @Inject
    private EngineManager engineManager;

    Map<String, UciAgent> agents = new TreeMap<>();
    // TODO: czy obslugiwac wiele sesji tego samego agenta?
    Map<Integer, UciAgent> agentsById = new HashMap<>();

    @Schedule(persistent = false, hour = "*", minute = "*", second = "*/20")
    public void checkIfAlive() {
        int idleSeconds = 20;
        for (UciAgent value : agents.values()) {
            value.heartbeat(idleSeconds);
        }
    }

    public void register(String sessionId, UciAgent agent) {
        agents.put(sessionId, agent);
        agentsById.put(agent.getAgentEntity().getId(), agent);
        engineManager.updateLastConnection(agent.getAgentEntity(), LocalDateTime.now());
    }

    public void unregister(String sessionId) {
        UciAgent agent = agents.get(sessionId);
        agents.remove(sessionId);
        if(agent!= null) {
            agentsById.remove(agent.getAgentEntity().getId());
        }
    }
    
    public Optional<IAgent> find(Engine engine) {
        if (engine.getType() != EngineType.HYBRID) {
            return Optional.ofNullable(agentsById.get(engine.getId()));
        }
        logger.info("Hybrid agent has {} subagents", engine.getSubEngines());
        List<HybridAgent.HybridSubAgent> subAgents = new ArrayList<>();
        for (SubEnginesRelation subEnginesRelation : engine.getSubEnginesRelations()) {
            EngineConfig subEngine = subEnginesRelation.getSubEngine();
            logger.info("Checking for {} if subengine is online {}", engine.getId(), subEngine.getEngine().getId());
            UciAgent subAgent = agentsById.get(subEngine.getEngine().getId());
            if (subAgent == null) {
                return Optional.empty();
            }
            subAgents.add(new HybridAgent.HybridSubAgent(subAgent, subEngine, subEnginesRelation.getGamePhase()));
        }
        return Optional.of(new HybridAgent(engine, subAgents));
    }

    public UciAgent get(String sessionId) {
        UciAgent agent = agents.get(sessionId);
        if (agent == null) {
            throw new IllegalArgumentException("No agent with id=" + sessionId);
        }
        return agent;
    }

    public List<String> list() {
        return new ArrayList<>(agents.keySet());
    }
}
