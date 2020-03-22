package net.purevirtual.chell.central.web.agent.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class AgentsManager {

    Map<String, UciAgent> agents = new TreeMap<>();

    @Schedule(persistent = false, hour = "*", minute = "*", second = "*/20")
    public void checkIfAlive() {
        int idleSeconds = 20;
        for (UciAgent value : agents.values()) {
            value.heartbeat(idleSeconds);
        }
    }

    public void register(String sessionId, UciAgent agent) {
        agents.put(sessionId, agent);
    }

    public void unregister(String sessionId) {
        agents.remove(sessionId);
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
