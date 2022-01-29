import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*
-Djavax.net.ssl.keyStore=resources/serverkeystore.jks
-Djavax.net.ssl.keyStorePassword=changeit
-Djavax.net.ssl.trustStore=resources/servertruststore.jks
-Djavax.net.ssl.trustStorePassword=changeit
 */

public class ServerWithVMOptions {

    private static final int SERVER_PORT = 8443;

    public static void main(String[] args) {

        ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
        try (ServerSocket listener = factory.createServerSocket(SERVER_PORT)) {
            SSLServerSocket sslListener = (SSLServerSocket) listener;
            sslListener.setNeedClientAuth(true);
            sslListener.setEnabledCipherSuites(
                    new String[] { "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256" });
            sslListener.setEnabledProtocols(
                    new String[] { "TLSv1.2" });
            while (true) {
                try (Socket socket = sslListener.accept()) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("Hello from server!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
