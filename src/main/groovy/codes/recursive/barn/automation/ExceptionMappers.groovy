package codes.recursive.barn.automation

import groovy.json.JsonGenerator

import javax.annotation.Priority
import javax.ws.rs.ServerErrorException
import javax.ws.rs.NotFoundException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Priority(0)
@Provider
public class VanillaExceptionMapper implements ExceptionMapper<ServerErrorException> {
    @Override
    public Response toResponse(ServerErrorException ex) {
        println 'VanillaExceptionMapper'
        def generator = new JsonGenerator.Options().build()
        return Response.status(500).entity(generator.toJson([exception: ex])).type("application/json").build()
    }
}

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException ex) {
        println 'RuntimeExceptionMapper'
        def generator = new JsonGenerator.Options().build()
        return Response.status(500).entity(generator.toJson([exception: ex])).type("application/json").build()
    }
}

@Provider
public class ErrorMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        println 'ErrorMapper'
        def generator = new JsonGenerator.Options().build()
        return Response.status(500).entity(generator.toJson([exception: ex])).type("application/json").build()
    }
}

@Provider
public class PageNotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException ex) {
        println 'PageNotFoundExceptionMapper'
        return Response.temporaryRedirect(new URI('/barn/404')).build()
    }
}