package net.purevirtual.chell.central.web.agent.control;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.websocket.RemoteEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsAgentInput implements AgentInput {
    private static final Logger logger = LoggerFactory.getLogger(WsAgentInput.class);
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

    @Override
    public void heartbeat() {
        ByteBuffer ping = ByteBuffer.wrap("PING".getBytes(StandardCharsets.UTF_8));
        try {
            remote.sendPing(ping);
        } catch (IOException|IllegalArgumentException ex) {
            logger.warn("failed to send ping ", ex);
        } 
    }
    
    

}
