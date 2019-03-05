package codes.recursive.barn.automation.service

import org.eclipse.microprofile.config.inject.ConfigProperty

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class TestService {
    def c = new Date()
    String incomingTopic
    String incomingServer
    String outgoingTopic
    String outgoingServer

    @Inject
    TestService(
            @ConfigProperty(name = "app.barn.incomingTopic") String incomingTopic,
            @ConfigProperty(name = "app.barn.incomingBootstrapServer") String incomingServer,
            @ConfigProperty(name = "app.barn.outgoingTopic") String outgoingTopic,
            @ConfigProperty(name = "app.barn.outgoingBootstrapServer") String outgoingServer
    ) {
        this.incomingTopic = incomingTopic
        this.incomingServer = incomingServer
        this.outgoingTopic = outgoingTopic
        this.outgoingServer = outgoingServer
    }

    def test() {
        return [
                incomingTopic: incomingTopic,
                incomingServer: incomingServer,
                outgoingTopic: outgoingTopic,
                outgoingServer: outgoingServer,
                resourceCreated: c,
        ]
    }
}
