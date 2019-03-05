package codes.recursive.barn.automation

import codes.recursive.barn.automation.filter.CorsFilter
import codes.recursive.barn.automation.service.data.DataService
import codes.recursive.barn.automation.service.data.OracleDataService
import codes.recursive.barn.automation.service.kafka.MessageConsumerService
import codes.recursive.barn.automation.service.kafka.MessageProducerService
import groovy.util.logging.Slf4j
import io.helidon.common.CollectionsHelper

import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

@ApplicationScoped
@ApplicationPath("/")
@Slf4j
class BarnApplication extends Application {

    @Inject private MessageConsumerService messageConsumerService
    @Inject private MessageProducerService messageProducerService
    @Inject private DataService dataService
    @Inject private OracleDataService oracleDataService

    @PostConstruct
    void init() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            void run() {
                log.info("Shutting down...")
                oracleDataService.close()
                messageConsumerService.close()
                messageProducerService.close()
            }
        })

        Thread.start {
            messageConsumerService.start()
        }
    }

    @Override
    Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(
                CorsFilter.class,
                PageResource.class,
                BarnResource.class,
                PageNotFoundExceptionMapper.class,
                RuntimeExceptionMapper.class,
                VanillaExceptionMapper.class,
                ErrorMapper.class
        )
    }

}
