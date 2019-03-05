package codes.recursive.barn.automation


import groovy.util.logging.Slf4j
import io.helidon.microprofile.server.Server

@Slf4j
final class Main {

    private Main() { }

    static void main(final String[] args) throws IOException {
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
}
