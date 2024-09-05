package com.dummycompany.test.framework.core.api;

import com.dummycompany.test.framework.core.Constants;
import okhttp3.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class ApiHandler {
    private static final Logger LOG = LogManager.getLogger(ApiHandler.class);
    final static String passphrase = "twmsapi";
    final static int timeout = 120;

    public static Response getApiResponseWithCertHandle(Request request, String certFileName) {
        Response rs = null;
        try {
            OkHttpClient client = handleCertificate(certFileName);

            rs = client.newCall(request).execute();
            return rs;
        } catch (Exception e) {
            LOG.error("Error occured while handling Api response with certificate.");
        }
        return rs;
    }

    public static Response getApiResponse(Request request) {
        Response response = null;

        try {
            System.getProperties().put("http.proxyHost", "");
            System.getProperties().put("http.proxyPort", "");
            System.getProperties().put("https.proxyHost", "");
            System.getProperties().put("https.proxyPort", "");
            response = ApiCore.getUnsafeOkHttpClient().newCall(request).execute();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        if (response == null) {
            try {
                System.getProperties().put("http.proxyHost", "bcani.tcif.dummycompany.com.au");
                System.getProperties().put("http.proxyPort", "8080");
                System.getProperties().put("https.proxyHost", "bcani.tcif.dummycompany.com.au");
                System.getProperties().put("https.proxyPort", "8080");
                System.getProperties().put("http.proxyHost", "bcavi.tcif.dummycompany.com.au");
                System.getProperties().put("http.proxyPort", "8080");
                System.getProperties().put("https.proxyHost", "bcavi.tcif.dummycompany.com.au");
                System.getProperties().put("https.proxyPort", "8080");

                response = ApiCore.getUnsafeOkHttpClient().newCall(request).execute();
            } catch (IOException e) {
                LOG.error("Error: " + e.toString());
            }
        }

        System.getProperties().put("http.proxyHost", "");
        System.getProperties().put("http.proxyPort", "");
        System.getProperties().put("https.proxyHost", "");
        System.getProperties().put("https.proxyPort", "");
        return response;
    }


    public static Response getSFDCApiResponse(Request request) {
        Response response = null;
        try {
            System.getProperties().put("http.proxyHost", "bcani.tcif.dummycompany.com.au");
            System.getProperties().put("http.proxyPort", "8080");
            System.getProperties().put("https.proxyHost", "bcani.tcif.dummycompany.com.au");
            System.getProperties().put("https.proxyPort", "8080");

            response = ApiCore.getUnsafeOkHttpClient().newCall(request).execute();
        } catch (IOException e) {
            LOG.error("Error: " + e.toString());
        }

        System.getProperties().put("http.proxyHost", "");
        System.getProperties().put("http.proxyPort", "");
        System.getProperties().put("https.proxyHost", "");
        System.getProperties().put("https.proxyPort", "");
        return response;
    }


    public static OkHttpClient handleCertificate(String fileName) {
        SSLSocketFactory factory = null;
        TrustManager[] trustManagers = null;
        SSLContext ctx = null;
        X509TrustManager trustManager = null;
        OkHttpClient client;

        //hardcoded- need to remove
        System.setProperty("javax.net.ssl.trustStore", "./resources/certificates/" + fileName);
        System.setProperty("javax.net.ssl.keyStorePassword", "twmsapi");

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_3)
                .cipherSuites(CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256)//
                .allEnabledTlsVersions().supportsTlsExtensions(false).allEnabledCipherSuites()//
                .build();

        try {
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] certPassphrase = passphrase.toCharArray();

            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            ks = KeyStore.getInstance("JKS");
            FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "\\resources\\certificates\\" + fileName);
            ks.load(fileInputStream,certPassphrase);
            kmf.init(ks, certPassphrase);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(ks);

            trustManagers = trustManagerFactory.getTrustManagers();
            trustManager = (X509TrustManager) trustManagers[0];

            ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(kmf.getKeyManagers(), trustManagers, new java.security.SecureRandom());
            factory = ctx.getSocketFactory();
            fileInputStream.close();
        } catch (Exception e) {
            LOG.error(e.toString());
        }

        client = new OkHttpClient.Builder().readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS).sslSocketFactory(factory, trustManager)
                .proxyAuthenticator(ApiCore.proxyAuthenticator).connectionSpecs(Collections.singletonList(spec))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();

        return client;

    }

    /*This Methoed is used to get the index of object which is present in the array
        Params
        responseData  - is String of json object or json array object
        arrayNode is optional params - if response Data is json array then this should be null other wise provide the node tag
        key
        value
     */
    public static int getIndexFromJson(String arrayNode, String key, String value, String responseData) throws Exception {
        int index = -1;
        org.json.JSONArray jsonArray;
        if (StringUtils.isNotBlank(arrayNode)) {
            jsonArray = new org.json.JSONArray(getJsonTagValue(arrayNode, responseData));
        } else {
            jsonArray = new org.json.JSONArray(responseData);
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getJSONObject(i).get(key).toString().equalsIgnoreCase(value)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static String getJsonTagValue(String searchJsonTag, String responseData) throws Exception {
        Object mainValueObject;
        String[] tags = searchJsonTag.split("\\.");
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(responseData);
        try {
            for (int i = 0; i < tags.length; i++) {
                if (i != tags.length - 1) {
                    if (tags[i].contains("[")) {
                        int NodeIndex = extractNodeIndex(tags[i]);
                        String[] tag = tags[i].split("\\[");
                        JSONArray JsonArryobj = (JSONArray) obj.get(tag[0]);
                        obj = (JSONObject) JsonArryobj.get(NodeIndex);
                    } else {
                        obj = (JSONObject) obj.get(tags[i]);
                    }
                } else {
                    if (tags[i].contains("[")) {
                        int NodeIndex = extractNodeIndex(tags[i]);
                        String[] tag = tags[i].split("\\[");
                        JSONArray JsonArryobj = (JSONArray) obj.get(tag[0]);
                        Object value = JsonArryobj.get(NodeIndex);
                        return value.toString();
                    } else {
                        mainValueObject = obj.get(tags[i]);
                        return mainValueObject.toString();
                    }
                }
            }
        } catch (Exception e) {
            return "* Specified Json tag '" + searchJsonTag + "' not found in the provided response data. ";
        }
        return "** Specified Json tag '" + searchJsonTag + "' not found in the provided response data";
    }

    @SuppressWarnings("unchecked")
    public static String setJsonTagValue(String searchJsonTag, String responseData, Object tagValue) {
        String[] tags = searchJsonTag.split("\\.");
        JSONParser parser = new JSONParser();
        JSONObject jObj = null;

        try {
            jObj = (JSONObject) parser.parse(responseData);
        } catch (ParseException e1) {
            LOG.error("Error occured while parsing Json reponse to search tag value- " + StringEscapeUtils.escapeJava(searchJsonTag));
        }

        JSONObject tempObj = jObj;
        try {
            for (int i = 0; i < tags.length; i++) {
                if (i != tags.length - 1) {
                    if (tags[i].contains("[")) {
                        int NodeIndex = extractNodeIndex(tags[i]);
                        String[] tag = tags[i].split("\\[");
                        JSONArray JsonArryobj = (JSONArray) Objects.requireNonNull(jObj).get(tag[0]);
                        jObj = (JSONObject) JsonArryobj.get(NodeIndex);
                    } else {
                        jObj = (JSONObject) Objects.requireNonNull(jObj).get(tags[i]);
                    }
                } else {
                    if (tags[i].contains("[")) {
                        int NodeIndex = extractNodeIndex(tags[i]);
                        String[] tag = tags[i].split("\\[");
                        JSONArray JsonArryobj = (JSONArray) Objects.requireNonNull(jObj).get(tag[0]);
                        JsonArryobj.set(NodeIndex, tagValue);
                    } else {
                        Objects.requireNonNull(jObj).put(tags[i], tagValue);
                    }
                }
            }
        } catch (Exception e) {
            return "Specified Json tag '" + searchJsonTag + "' not found in the provided response data";
        }
        return Objects.requireNonNull(tempObj).toString();
    }

    private static int extractNodeIndex(String node) {
        String[] nodeSplit = node.split("\\[");
        String[] indexSplit = nodeSplit[1].split("\\]");
        return Integer.parseInt(indexSplit[0]);
    }

}