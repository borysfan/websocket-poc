package com.gft.person;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by e-dkbs on 18/07/2017.
 */
public class SSLWebSocketClient extends StandardWebSocketClient {

    public SSLWebSocketClient() {
        super();
        try {

            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(new File("keystore.p12"), "tomcat".toCharArray(), new TrustSelfSignedStrategy())
                    .build();
            Map<String, Object> properties = new HashMap<>();
            properties.put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);
            this.setUserProperties(properties);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }
}
