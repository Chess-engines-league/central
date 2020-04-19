package net.purevirtual.chell.central.web.boundary;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/gui")
public class GuiRestConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(IndexPage.class);
        classes.add(MatchPage.class);
        classes.add(GamePage.class);
        classes.add(EnginePage.class);
        return classes;
    }

}
