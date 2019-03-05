package codes.recursive.barn.automation.service.data

import codes.recursive.barn.automation.model.BarnEvent
import groovy.json.JsonSlurper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.eclipse.microprofile.config.inject.ConfigProperty

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import java.sql.SQLException

@ApplicationScoped
@Slf4j
class OracleDataService {
    String oracleUrl
    String oracleUser
    String oraclePassword
    Sql sql

    @Inject
    OracleDataService(
            @ConfigProperty(name = "app.oracle.url") String url,
            @ConfigProperty(name = "app.oracle.user") String user,
            @ConfigProperty(name = "app.oracle.password") String password
    ) {
        this.oracleUrl = url
        this.oracleUser = user
        this.oraclePassword = password
        this.sql = getSql()
    }

    Sql getSql() throws SQLException {
        def db = [url: this.oracleUrl, user: this.oracleUser, password: this.oraclePassword, driver: 'oracle.jdbc.driver.OracleDriver']
        Sql sql = Sql.newInstance( db.url, db.user, db.password, db.driver )
        return sql
    }

    def save(BarnEvent barnEvent) {
        List params = [barnEvent.type, barnEvent.data, barnEvent.capturedAt.format('yyyy-MM-dd HH:mm:ss')]
        sql.execute("""
          insert into BARN.BARN_EVENT (TYPE, DATA, CAPTURED_AT) values (?, ?, to_timestamp(?, 'yyyy-mm-dd HH24:mi:ss'))
        """, params)
    }

    int countEvents() {
        return sql.firstRow("select count(1) as NUM from BARN_EVENT").NUM
    }

    int countEventsByEventType(String type) {
        return sql.firstRow("select count(1) as NUM from BARN_EVENT where TYPE = ?", [type]).NUM
    }

    List listEventsByEventType(String type, int offset=0, int max=50) {
        List events = []
        sql.rows("select * from BARN_EVENT where TYPE = ?", [type], offset, max) {
            events << [
                    id: it?.ID,
                    type: it?.TYPE,
                    capturedAt: it?.CAPTURED_AT.dateValue(),
                    data:  slurper.parseText(it?.DATA?.asciiStream?.text),
            ]
        }
        return events
    }

    List listEvents(offset=0,max=50) {
        List events = []
        JsonSlurper slurper = new JsonSlurper()
        sql.eachRow("select * from BARN_EVENT", offset, max) {
            events << [
                    id: it?.ID,
                    type: it?.TYPE,
                    capturedAt: it?.CAPTURED_AT.dateValue(),
                    data:  slurper.parseText(it?.DATA?.asciiStream?.text),
            ]
        }
        return events
    }

    void close() {
        this.sql.close()
    }
}
