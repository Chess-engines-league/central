package net.purevirtual.chell.central.web.entity;

import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
@Lock(LockType.READ)
public class Config {

    @Resource(lookup = "java:global/config/githubClientId")
    private String githubClientId;

    @Resource(lookup = "java:global/config/githubSecret")
    private String githubSecret;

    @Resource(lookup = "java:global/config/webRoot")
    private String webRoot;

    public String getGithubLoginUrl() {
        return "https://github.com/login/oauth/authorize?client_id=" + githubClientId;
    }

    public String getGithubClientId() {
        return githubClientId;
    }

    public String getGithubSecret() {
        return githubSecret;
    }

    public String getWebRoot() {
        return webRoot;
    }

}
