import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class ServerWithCode {

    private static final int SERVER_PORT = 8443;
    private static final String PATH_TO_SERVER_KEYSTORE = "/usr/absolute_path/resources/serverkeystore.jks";
    private static final String SERVER_KEYSTORE_PASS = "changeit";
    private static final String PATH_TO_SERVER_TRUSTSTORE = "/usr/absolute_path/resources/servertruststore.jks";
    private static final String SERVER_TRUSTSTORE_PASS = "changeit";

    public static void main(String[] args) throws KeyStoreException, IOException,
            UnrecoverableKeyException, NoSuchAlgorithmException,
            CertificateException, KeyManagementException {

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(
                new FileInputStream(PATH_TO_SERVER_KEYSTORE),
                SERVER_KEYSTORE_PASS.toCharArray()
        );
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, SERVER_KEYSTORE_PASS.toCharArray());

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(
                new FileInputStream(PATH_TO_SERVER_TRUSTSTORE),
                SERVER_TRUSTSTORE_PASS.toCharArray()
        );
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                new SecureRandom()
        );

        ServerSocketFactory factory = sslContext.getServerSocketFactory();

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
                    out.println("Hello World from server!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
