package net.purevirtual.chell.central.web.agent.control;

public interface AgentInput {

    void send(String... messages);

    public void heartbeat();
}
