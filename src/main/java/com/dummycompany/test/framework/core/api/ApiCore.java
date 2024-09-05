package com.dummycompany.test.framework.core.api;

import com.dummycompany.test.framework.core.utils.Encoding;
import com.dummycompany.test.framework.core.utils.Props;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class ApiCore {
    private final static String user = Props.getEnvOrPropertyValue("proxyUser", null);
    private final static String password = Props.getEnvOrPropertyValue("proxyPassword");

    static Authenticator proxyAuthenticator = new Authenticator() {
        public Request authenticate(Route route, Response response) {
            String credential = Credentials.basic(user, Encoding.decodePassword(password));
            return response
                    .request()
                    .newBuilder()
                    .header("Proxy-Authorization", credential)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0")
                    .build();
        }
    };

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }};

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

            builder.hostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder
                    .connectTimeout(ApiHandler.timeout, TimeUnit.SECONDS)
                    .writeTimeout(ApiHandler.timeout, TimeUnit.SECONDS)
                    .readTimeout(ApiHandler.timeout, TimeUnit.SECONDS)
                    .proxyAuthenticator(proxyAuthenticator)
                    .build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
