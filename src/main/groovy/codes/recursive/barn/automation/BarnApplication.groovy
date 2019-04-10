package codes.recursive.barn.automation

import codes.recursive.barn.automation.event.EventEmitter
import codes.recursive.barn.automation.filter.CorsFilter
import codes.recursive.barn.automation.service.data.OracleDataService
import codes.recursive.barn.automation.service.streaming.CameraConsumerService
import codes.recursive.barn.automation.service.streaming.MessageConsumerService
import codes.recursive.barn.automation.service.streaming.MessageProducerService
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
    @Inject private CameraConsumerService cameraConsumerService
    @Inject private MessageProducerService messageProducerService
    @Inject private OracleDataService oracleDataService
    @Inject private EventEmitter eventEmitter

    @PostConstruct
    void init() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            void run() {
                log.info("Shutting down...")
                oracleDataService.close()
                messageConsumerService.close()
                cameraConsumerService.close()
                messageProducerService.close()
            }
        })
        Thread.start {
            messageConsumerService.start()
        }
        Thread.start {
            cameraConsumerService.start()
        }
    }

    @Override
    Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(
                CorsFilter.class,
                PageResource.class,
                BarnResource.class,
                ErrorMapper.class,
                VanillaExceptionMapper.class,
                PageNotFoundExceptionMapper.class,
                RuntimeExceptionMapper.class,
        )
    }

}
