package codes.recursive.barn.automation

import groovy.json.JsonGenerator

import javax.annotation.Priority
import javax.ws.rs.NotFoundException
import javax.ws.rs.ServerErrorException
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import java.util.logging.Level
import java.util.logging.Logger

@Priority(0)
@Provider
public class VanillaExceptionMapper implements ExceptionMapper<ServerErrorException> {
    private static final Logger logger = Logger.getLogger(VanillaExceptionMapper.class.name)
    @Context private UriInfo uriInfo
    @Override
    public Response toResponse(ServerErrorException ex) {
        logger.log(Level.SEVERE, "VanillaExceptionMapper", ex)
        def generator = new JsonGenerator.Options().build()
        return Response.status(500).entity(generator.toJson([message: ex?.message, cause: ex?.cause])).type("application/json").build()
    }
}

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
    private static final Logger logger = Logger.getLogger(RuntimeExceptionMapper.class.name)
    @Override
    public Response toResponse(RuntimeException ex) {
        logger.log(Level.SEVERE, "RuntimeExceptionMapper", ex)
        def generator = new JsonGenerator.Options().build()
        return Response.status(500).entity(generator.toJson([message: ex?.message, cause: ex?.cause])).type("application/json").build()
    }
}

@Provider
public class ErrorMapper implements ExceptionMapper<Throwable> {
    private static final Logger logger = Logger.getLogger(ErrorMapper.class.name)
    @Context private UriInfo uriInfo
    @Override
    public Response toResponse(Throwable ex) {
        logger.log(Level.SEVERE, "ErrorMapper", ex)
        def generator = new JsonGenerator.Options().build()
        return Response.status(500).entity(generator.toJson([message: ex?.message, cause: ex?.cause])).type("application/json").build()
    }
}

@Provider
public class PageNotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    private static final Logger logger = Logger.getLogger(PageNotFoundExceptionMapper.class.name)
    @Context private UriInfo uriInfo
    @Override
    public Response toResponse(NotFoundException ex) {
        logger.log(Level.SEVERE, "Could not find ${this.uriInfo.path}", [message: ex?.message, cause: ex?.cause])
        return Response.temporaryRedirect(new URI('/barn/404')).build()
    }
}