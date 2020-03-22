package net.purevirtual.chell.central.web.agent.bounduary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import net.purevirtual.chell.central.web.agent.control.AgentInput;
import net.purevirtual.chell.central.web.agent.control.AgentsManager;
import net.purevirtual.chell.central.web.agent.control.UciAgent;
import net.purevirtual.chell.central.web.agent.control.WsAgentInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/agent/{username}")
public class WebSocket {

    private static final Logger logger = LoggerFactory.getLogger(WebSocket.class);
    private UciAgent uciAgent;
    private static Set<WebSocket> chatEndpoints
            = new CopyOnWriteArraySet<>();
    @Inject
    private AgentsManager agentsManager;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {

        chatEndpoints.add(this);
        logger.info("connected: " + username);
        WsAgentInput wsAgentInput = new WsAgentInput(session.getAsyncRemote());
        uciAgent = new UciAgent(wsAgentInput);
        agentsManager.register(session.getId(), uciAgent);
    }

    @OnMessage
    public void onMessage(Session session, String message)
            throws IOException {
        logger.debug("Got message: '{}', handing to agent {}", message.trim(), session.getId());
        uciAgent.onMessage(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        agentsManager.unregister(session.getId());
        logger.info("disconnected: ");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("Encountered error, disconnecting", throwable);
        agentsManager.unregister(session.getId());
        try {
            session.close();
        } catch (IOException ex) {
            logger.warn("Failed to close connection cleanly");
        }
    }
}
