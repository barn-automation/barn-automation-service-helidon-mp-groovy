package codes.recursive.barn.automation.service.data


import codes.recursive.barn.automation.model.BarnEvent
import com.mongodb.MongoClient
import groovy.util.logging.Slf4j
import org.eclipse.microprofile.config.inject.ConfigProperty
import xyz.morphia.Datastore
import xyz.morphia.Morphia
import xyz.morphia.query.FindOptions

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
@Slf4j
class DataService {
    Morphia morphia = new Morphia()
    Datastore datastore
    MongoClient client

    @Inject
    DataService(
            @ConfigProperty(name = "app.mongo.host") String mongoHost,
            @ConfigProperty(name = "app.mongo.port") String mongoPort,
            @ConfigProperty(name = "app.mongo.db") String mongoDbName) {

        morphia.mapPackage("codes.recursive.barn.automation.model")
        client = new MongoClient(mongoHost, mongoPort as int)
        datastore = morphia.createDatastore(client, mongoDbName)
        datastore.ensureIndexes()
    }

    def countEvents() {
        return datastore.createQuery(BarnEvent.class).count()
    }

    def countEventsByEventType(String type) {
        return datastore.createQuery(BarnEvent.class).field("type").equal(type).count()
    }

    def listEventsByEventType(String type, int offset=0, int max=50) {
        return datastore.createQuery(BarnEvent.class).field("type").equal(type).order("-capturedAt")
                .asList( new FindOptions().skip(offset).limit(max) )
    }

    def listEvents(offset=0,max=50) {
        return datastore.createQuery(BarnEvent.class).order("-capturedAt")
                .asList( new FindOptions().skip(offset).limit(max) )
    }

    def close() {
        client.close()
    }
}
