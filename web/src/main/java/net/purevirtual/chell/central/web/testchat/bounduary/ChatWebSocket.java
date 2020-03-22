package net.purevirtual.chell.central.web.testchat.bounduary;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

@ServerEndpoint(value = "/chat/{username}")
public class ChatWebSocket {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocket.class);
    private Session session;
    private static Set<ChatWebSocket> chatEndpoints
            = new CopyOnWriteArraySet<>();
    private static ConcurrentMap<String, String> users = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        if (users.values().contains(username)) {
            throw new IOException("User name already in use");
        }
        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);

        broadcast("**connected: " + username);
    }

    @OnMessage
    public void onMessage(Session session, String message)
            throws IOException {
        broadcast(" " + message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String message = "**disconnected: " + users.get(session.getId());
        chatEndpoints.remove(this);
        users.remove(session.getId());
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable
    ) throws IOException {
        logger.error("Encountered error in session {} for user {}", session.getId());
        session.close();
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
