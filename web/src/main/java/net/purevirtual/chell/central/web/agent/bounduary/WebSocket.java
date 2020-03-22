package net.purevirtual.chell.central.web.agent.bounduary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/agent/{username}")
public class WebSocket {

    private static final Logger logger = LoggerFactory.getLogger(WebSocket.class);
    private Session session;
    private static Set<WebSocket> chatEndpoints
            = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {

        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);

        broadcast("connected: " + username);
    }

    @OnMessage
    public void onMessage(Session session, String message)
            throws IOException {
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        chatEndpoints.remove(this);

        broadcast("Disconnected!");
    }

    @OnError
    public void onError(Session session, Throwable throwable
    ) {
        // Do error handling here
    }

    private static void broadcast(String message)
            throws IOException {

        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendText(message);
                } catch (IOException ex) {
                    logger.error(message);
                }

            }
        });
    }
}
