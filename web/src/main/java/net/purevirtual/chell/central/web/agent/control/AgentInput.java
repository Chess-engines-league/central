package net.purevirtual.chell.central.web.agent.control;

import java.util.List;

public interface AgentInput {

    void send(List<String> messages);
    void send(String... messages);

    public void heartbeat();
}
