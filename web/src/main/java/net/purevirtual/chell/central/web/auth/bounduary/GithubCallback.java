package net.purevirtual.chell.central.web.auth.bounduary;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.bind.annotation.JsonbProperty;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.purevirtual.chell.central.web.crud.control.UserManager;
import net.purevirtual.chell.central.web.entity.Config;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/oauth/callback/github")
public class GithubCallback {

    private static final Logger logger = LoggerFactory.getLogger(GithubCallback.class);
    @Context
    private HttpServletRequest servletContext;

    @Inject
    private Config config;

    @Inject
    private UserManager userManager;

    @GET
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Response callback(@QueryParam("code") String code) throws IOException, URISyntaxException {
        try {
            AccessTokenRequest request = new AccessTokenRequest();
            request.clientId = config.getGithubClientId();
            request.clientSecret = config.getGithubSecret();
            request.code = code;
            Client client = ClientBuilder.newClient();
            AccessTokenResponse response = client.target("https://github.com/login/oauth/access_token")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(request), AccessTokenResponse.class);
            logger.info("gitHub response: {}", response);

            GitHubClient apiClient = new GitHubClient();
            apiClient.setOAuth2Token(response.accessToken);
            UserService userService = new UserService(apiClient);
            User user = userService.getUser();

            String effectiveLogin = "github.com:" + user.getLogin();
            userManager.create(effectiveLogin);
            Integer userId = userManager.getByLogin(effectiveLogin).getId();

            HttpSession session = servletContext.getSession();
            logger.info("EffectiveLogin: {}, userId: {}, sessionId: {}", effectiveLogin, userId, session.getId());
            session.setAttribute("userId", userId);
            
            URI uri = new URI(config.getWebRoot());
            return Response.temporaryRedirect(uri).build();
        } catch (Exception ex) {
            logger.error("Failed to auth github user", ex);
            URI uri = new URI(config.getWebRoot());
            return Response.temporaryRedirect(uri).build();
        }

    }

    public static class AccessTokenRequest {

        @JsonbProperty(value = "client_id")
        private String clientId;
        @JsonbProperty(value = "client_secret")
        private String clientSecret;
        @JsonbProperty(value = "code")
        private String code;
    }

    public static class AccessTokenResponse {

        @JsonbProperty(value = "access_token")
        private String accessToken;
        private String scope;
        @JsonbProperty(value = "token_type")
        private String tokenType;
        private String error;
        @JsonbProperty(value = "error_description")
        private String errorDescription;

        @Override
        public String toString() {
            return "AccessTokenResponse{" + "accessToken=" + accessToken + ", scope=" + scope + ", tokenType=" + tokenType + ", error=" + error + ", errorDescription=" + errorDescription + '}';
        }

    }
}
