package codes.recursive.barn.automation

import groovy.json.JsonGenerator

import javax.enterprise.context.RequestScoped
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@RequestScoped
class PageResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    def getDefaultMessage() {
        return new JsonGenerator.Options().build().toJson([ health: "OK", at: new Date(), source: 'helidon-groovy-mp' ])
    }

    @Path("/404")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    def notFound() {
        return "not found"
    }
}
