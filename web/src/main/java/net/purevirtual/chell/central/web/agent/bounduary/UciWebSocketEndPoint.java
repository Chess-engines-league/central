package net.purevirtual.chell.central.web.agent.bounduary;

import java.io.IOException;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import net.purevirtual.chell.central.web.agent.control.LiveAgentsManager;
import net.purevirtual.chell.central.web.agent.control.UciAgent;
import net.purevirtual.chell.central.web.agent.control.WsAgentInput;
import net.purevirtual.chell.central.web.crud.control.EngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/agent/uci/{token}")
public class UciWebSocketEndPoint {

    private static final Logger logger = LoggerFactory.getLogger(UciWebSocketEndPoint.class);
    private UciAgent uciAgent;

    @Inject
    private LiveAgentsManager agentsManager;
    
    @Inject
    private EngineManager agentManager;

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        agentManager.findByToken(token).ifPresentOrElse(agent-> {
            logger.info("connected: {} ", agent.getId());
            WsAgentInput wsAgentInput = new WsAgentInput(session.getAsyncRemote());
            uciAgent = new UciAgent(wsAgentInput, agent);
            agentsManager.register(session.getId(), uciAgent);
        },  () -> {
            try {
                logger.warn("unknown agent token: '{}', disconnecting", token);
                session.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "unkown agent token"));
            } catch (IOException ex) {
                logger.warn("error while disconnecting", ex);
            }
        });
        
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String trimmedMessage = message.trim();
        logger.debug("Got message: '{}', handing to agent {}", trimmedMessage, session.getId());
        //logger.info("Got message: '{}', handing to agent {}", trimmedMessage, session.getId());
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
