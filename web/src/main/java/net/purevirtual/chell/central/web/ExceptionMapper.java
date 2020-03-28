package net.purevirtual.chell.central.web;

import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionMapper.class);

    @Override
    public Response toResponse(Exception ex) {
        if (ex instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) ex;
            return webEx.getResponse();
        }
        String errorID = UUID.randomUUID().toString();
        logger.error("Unhandled error: {}", errorID, ex);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal error " + errorID).type("text/plain").build();
    }
}
