package net.purevirtual.chell.central.web;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import net.purevirtual.chell.central.web.agent.bounduary.EngineResource;
import net.purevirtual.chell.central.web.agent.bounduary.GameResource;
import net.purevirtual.chell.central.web.auth.bounduary.GithubCallback;

@ApplicationPath("/rest")
public class JAXRSConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(GithubCallback.class);
        classes.add(EngineResource.class);
        classes.add(GameResource.class);
        return classes;
    }

}
