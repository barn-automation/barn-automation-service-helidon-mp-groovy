package codes.recursive.barn.automation


import groovy.util.logging.Slf4j
import io.helidon.microprofile.server.Server

import java.util.logging.LogManager

@Slf4j
final class Main {

    private Main() { }

    static void main(final String[] args) throws IOException {
        setupLogging()
        startServer()
    }

    protected static Server startServer() throws IOException {
        Server server = Server.create()
        server.start()
        def art = """
                             +&-
                           _.-^-._    .--.
                        .-'   _   '-. |__|
                       /     |_|     \\|  |
                      /               \\  |
                     /|     _____     |\\ |
                      |    |==|==|    |  |
  |---|---|---|---|---|    |--|--|    |  |
  |---|---|---|---|---|    |==|==|    |  |
 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
"""
        println art
        return server
    }
    private static void setupLogging() throws IOException {
        LogManager.getLogManager().readConfiguration(
                Main.class.getResourceAsStream("/logging.properties"))
    }
}
