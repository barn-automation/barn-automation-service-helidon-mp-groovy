package codes.recursive.barn.automation

import codes.recursive.barn.automation.event.EventEmitter
import codes.recursive.barn.automation.service.TestService
import codes.recursive.barn.automation.service.data.OracleDataService
import codes.recursive.barn.automation.service.streaming.MessageConsumerService
import codes.recursive.barn.automation.service.streaming.MessageProducerService
import groovy.json.JsonGenerator
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.sse.OutboundSseEvent
import javax.ws.rs.sse.Sse
import javax.ws.rs.sse.SseBroadcaster
import javax.ws.rs.sse.SseEventSink
import java.util.logging.Logger

@Path("/barn")
@RequestScoped
class BarnResource {

    Sse sse
    OutboundSseEvent.Builder eventBuilder
    SseBroadcaster sseBroadcaster

    @Inject private TestService testService
    @Inject private OracleDataService oracleDataService
    @Inject private MessageConsumerService messageConsumerService
    @Inject private MessageProducerService messageProducerService
    @Inject private EventEmitter eventEmitter

    @Context
    void setSse(Sse sse) {
        this.sse = sse
        this.eventBuilder = sse.newEventBuilder()
    }

    @PostConstruct
    void postConstruct() {
        this.sseBroadcaster = sse.newBroadcaster()
    }

    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    def getDefaultMessage() {
        return new JsonGenerator.Options().build().toJson([
                health: 'OK',
                streamSource: 'OCI',
                at: new Date(),
        ])
    }

    @Path("/events/type/{type}/{offset}/{max}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    def listEventsByTypePaginated(@PathParam("type") String type, @PathParam("offset") int offset, @PathParam("max") int max) {
        return new JsonGenerator.Options().build().toJson(
                oracleDataService.listEventsByEventType(type, offset, max)
        )
    }

    @Path("/events/type/{type}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    def listEventsByType(@PathParam("type") String type) {
        return new JsonGenerator.Options().build().toJson(
                oracleDataService.listEventsByEventType(type)
        )
    }

    @Path("/events/count/{type}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    def countEventsByType(@PathParam("type") String type) {
        return new JsonGenerator.Options().build().toJson(
                [
                        total: oracleDataService.countEventsByEventType(type),
                ]
        )
    }

    @Path("/events/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    def countEvents(@PathParam("type") String type) {
        return new JsonGenerator.Options().build().toJson(
                [
                        total: oracleDataService.countEvents(),
                ]
        )
    }

    @Path("/events/{offset}/{max}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    def listEventsPaginated(@PathParam("offset") int offset, @PathParam("max") int max) {
        return new JsonGenerator.Options().build().toJson(
                oracleDataService.listEvents(offset, max)
        )
    }

    @Path("/events")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    def getEvents() {
        return new JsonGenerator.Options().build().toJson(
                oracleDataService.listEvents()
        )
    }

    @Path("/control")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    def sendControlMessage(String message) {
        println "Received message: ${message}"
        messageProducerService.send(message)
        return new JsonGenerator.Options().build().toJson(
                [
                        sent: true
                ]
        )
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    void stream(@Context SseEventSink sseEventSink, @Context Sse sse) {
        def streaming = true
        this.sseBroadcaster.register(sseEventSink)
        def generator = new JsonGenerator.Options()
                .build()

        // send an initial event so the stream can connect
        // even if the client is offline
        OutboundSseEvent initEvent = this.eventBuilder
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(generator.toJson([ok: true]))
                .comment("All systems operational.  Ready to serve delicious events as they are available.")
                .build()
        this.sseBroadcaster.broadcast(initEvent)

        def messageHandler = { evt ->
            def id = UUID.randomUUID().toString()
            OutboundSseEvent sseEvent = this.eventBuilder
                    .id(id)
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .data(generator.toJson(evt))
                    .comment("Message incoming from barn at ${evt?.timestamp}")
                    .build()
            this.sseBroadcaster.broadcast(sseEvent)
            return true
        }

        eventEmitter.addListener('incomingMessage', messageHandler)

        while(streaming) {
            sleep(1000)
            if( sseEventSink.isClosed() ) {
                eventEmitter.removeListener('incomingMessage', messageHandler)
                this.sseBroadcaster.close()
                streaming = false
            }
        }
    }

    @Path("/404")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    def notFound() {
        return "not found"
    }
}
