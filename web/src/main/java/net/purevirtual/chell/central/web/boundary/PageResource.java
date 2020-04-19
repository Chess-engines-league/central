package net.purevirtual.chell.central.web.boundary;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public abstract class PageResource {

    @Inject
    private ServletContext servletContext;
    @Context
    private HttpServletRequest servletRequest;
    @Context
    private HttpServletResponse servletResponse;
    private TemplateEngine templateEngine;

    public PageResource() {
        templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setPrefix("/templates/");
        classLoaderTemplateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(classLoaderTemplateResolver);
    }

    protected TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    protected String process(String template, Map<String, Object> context) {
        return templateEngine.process(template, new org.thymeleaf.context.Context(null, context));
    }

}
