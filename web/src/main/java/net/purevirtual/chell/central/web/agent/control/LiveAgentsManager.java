package net.purevirtual.chell.central.web.agent.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import net.purevirtual.chell.central.web.crud.entity.Agent;

@Startup
@Singleton
public class LiveAgentsManager {

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
    }

    public void unregister(String sessionId) {
        UciAgent agent = agents.get(sessionId);
        agents.remove(sessionId);
        if(agent!= null) {
            agentsById.remove(agent.getAgentEntity().getId());
        }
    }
    
    public Optional<UciAgent> find(Agent agent) {
        return Optional.ofNullable(agentsById.get(agent.getId()));
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
