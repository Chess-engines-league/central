package net.purevirtual.chell.central.web.agent.bounduary;

import java.io.IOException;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import net.purevirtual.chell.central.web.agent.control.AgentsManager;
import net.purevirtual.chell.central.web.agent.control.UciAgent;
import net.purevirtual.chell.central.web.agent.control.WsAgentInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/agent/{username}")
public class UciWebSocketEndPoint {

    private static final Logger logger = LoggerFactory.getLogger(UciWebSocketEndPoint.class);
    private UciAgent uciAgent;

    @Inject
    private AgentsManager agentsManager;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        logger.info("connected: {}", username);
        WsAgentInput wsAgentInput = new WsAgentInput(session.getAsyncRemote());
        uciAgent = new UciAgent(wsAgentInput);
        agentsManager.register(session.getId(), uciAgent);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String trimmedMessage = message.trim();
        logger.debug("Got message: '{}', handing to agent {}", trimmedMessage, session.getId());
        uciAgent.onMessage(trimmedMessage);
    }

    @OnClose
    public void onClose(Session session) {
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
