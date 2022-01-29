import javax.net.SocketFactory;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/*
-Djavax.net.ssl.keyStore=resources/clientkeystore.jks
-Djavax.net.ssl.keyStorePassword=changeit
-Djavax.net.ssl.trustStore=resources/clienttruststore.jks
-Djavax.net.ssl.trustStorePassword=changeit
 */

public class ClientWithVMOptions {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8443;

    public static void main(String[] args) {
        connectToServer();
    }

    private static void connectToServer() {
        SocketFactory factory = SSLSocketFactory.getDefault();
        try (Socket connection = factory.createSocket(SERVER_HOST, SERVER_PORT)) {
            ((SSLSocket) connection).setEnabledCipherSuites(
                    new String[] { "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256" });
            ((SSLSocket) connection).setEnabledProtocols(
                    new String[] { "TLSv1.2" });

            SSLParameters sslParams = new SSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            ((SSLSocket) connection).setSSLParameters(sslParams);

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            System.out.println("Response from server: [" + input.readLine() + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
