import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class ClientWithCode {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8443;
    private static final String PATH_TO_CLIENT_KEYSTORE = "/usr/absolute_path/resources/clientkeystore.jks";
    private static final String CLIENT_KEYSTORE_PASS = "changeit";
    private static final String PATH_TO_CLIENT_TRUSTSTORE = "/usr/absolute_path/resources/clienttruststore.jks";
    private static final String CLIENT_TRUSTSTORE_PASS = "changeit";

    public static void main(String[] args) throws UnrecoverableKeyException, CertificateException,
            IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        connectToServer();
    }

    private static void connectToServer() throws IOException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(
                new FileInputStream(PATH_TO_CLIENT_KEYSTORE),
                CLIENT_KEYSTORE_PASS.toCharArray()
        );
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, CLIENT_KEYSTORE_PASS.toCharArray());

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(
                new FileInputStream(PATH_TO_CLIENT_TRUSTSTORE),
                CLIENT_TRUSTSTORE_PASS.toCharArray()
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

        SocketFactory factory = sslContext.getSocketFactory();

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
