package com.nippon.export.export;

import org.grails.web.json.JSONObject;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by hupanpan on 2017/1/13.
 */
public class WxUtils {
    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static String getUrlStr(String url, Map<String, String> params) {
        String paramStr = getParamStr(params);
        if (!url.equals("") && !paramStr.equals("")) {
            if (url.contains("?")) {
                if (url.substring(url.lastIndexOf("?")).equals("")) {
                    url += paramStr;
                } else {
                    url += "&" + paramStr;
                }
            } else {
                url += "?" + paramStr;
            }
        }
        return url;
    }

    private static String getParamStr(Map<String, String> params) {
        StringBuilder paramStr = new StringBuilder();
        if (params != null && params.size() > 0) {
            Set<String> keys = params.keySet();
            for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                paramStr.append(key);
                paramStr.append("=");
                paramStr.append(params.get(key));
                if (iterator.hasNext()) {
                    paramStr.append("&");
                }
            }
        }
        return paramStr.toString();
    }

    public static String doAll(String url, Map<String, String> params, String method) throws Exception {
        String result = "";
        JSONObject errorJson = new JSONObject();
        BufferedReader in = null;
        try {
//            System.setProperty("javax.net.ssl.keyStore", SERVER_KEY_STORE);
//            System.setProperty("javax.net.ssl.keyStorePassword", SERVER_KEY_STORE_PASSWORD);
//            System.setProperty("https.protocols", "SSLv3");
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
            String urlStr = getUrlStr(url, params);
            System.out.println(method + ":" + urlStr);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                    new java.security.SecureRandom());
            URL realUrl = new URL(urlStr);
            HttpsURLConnection connection = (HttpsURLConnection) realUrl.openConnection();
            connection.setRequestMethod(method);
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
            connection.setDoOutput(true);

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");


            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setReadTimeout(60 * 1000);
            connection.connect();

            int responseCode = connection.getResponseCode();
//            System.out.println("ResponseCode:" + responseCode);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
//            if (responseCode != 200) {
//                errorJson.put("errorCode",responseCode);
//                return errorJson.toString();
//
//            } else {
//
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;

    }
}
