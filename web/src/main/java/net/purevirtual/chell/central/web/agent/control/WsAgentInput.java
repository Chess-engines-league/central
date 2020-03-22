package net.purevirtual.chell.central.web.agent.control;

import javax.websocket.RemoteEndpoint;

public class WsAgentInput implements AgentInput {
    private RemoteEndpoint.Async remote;

    public WsAgentInput(RemoteEndpoint.Async remote) {
        this.remote = remote;
    }
    
    @Override
    public void send(String... messages) {
        for (String message : messages) {
            // we don't really care about result - if engine doesn't reply fast
            // enough or connections is dropped it will be killed and removed anyway
            remote.sendText(message);
        }
    }

}
