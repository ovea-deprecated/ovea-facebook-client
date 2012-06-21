/**
 * Copyright (C) 2011 Ovea <dev@ovea.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ovea.facebook.client;

import com.ovea.json.JSON;
import com.ovea.json.JSONArray;
import com.ovea.json.JSONObject;
import com.ovea.json.JSONType;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author David Avenante
 */
public final class DefaultFacebookClient implements FacebookClient {

    private static final Logger LOGGER = Logger.getLogger(DefaultFacebookClient.class.getName());

    private static final String ACCESS_TOKEN = "access_token";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final SSLSocketFactory sslSocketFactory;

    public DefaultFacebookClient(String client_id, String client_secret, String redirect_uri) {
        this.clientId = client_id;
        this.clientSecret = client_secret;
        this.redirectUri = redirect_uri;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
            sslSocketFactory = new SSLSocketFactory(sslContext);
            //noinspection deprecation
            sslSocketFactory.setHostnameVerifier(new X509HostnameVerifier() {
                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }

                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            throw new FacebookException(e.getMessage(), e);
        } catch (KeyManagementException e) {
            throw new FacebookException(e.getMessage(), e);
        }
    }

    @Override
    public FacebookToken accessToken(String verificationCode) throws FacebookException {
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("client_id", clientId));
        qparams.add(new BasicNameValuePair("client_secret", clientSecret));
        qparams.add(new BasicNameValuePair("code", verificationCode));
        qparams.add(new BasicNameValuePair("redirect_uri", redirectUri));
        JSONType result = get("https", "graph.facebook.com", "/oauth/access_token", qparams);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("accessToken for code " + verificationCode + " = " + result);
        }
        checkErrorOn(result);
        LOGGER.info("verification code : " + verificationCode + " / Response : " + result);
        String[] split = result.asObject().getString("data").split("&");
        return new FacebookToken(split[0].split("=")[1], Integer.parseInt(split[1].split("=")[1]));
    }

    @Override
    public JSONObject me(FacebookToken accessToken) throws FacebookException {
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair(ACCESS_TOKEN, accessToken.value()));
        JSONType result = get("https", "graph.facebook.com", "/me", qparams);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("me for token " + accessToken + " : " + result);
        }
        checkErrorOn(result);
        return result.asObject();
    }

    @Override
    public JSONArray friends(FacebookToken accessToken) throws FacebookException {
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair(ACCESS_TOKEN, accessToken.value()));
        JSONType result = get("https", "graph.facebook.com", "/me/friends", qparams);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("friends for token " + accessToken + " : " + result);
        }
        checkErrorOn(result);
        return result.asObject().getArray("data");
    }

    @Override
    public void publish(FacebookToken accessToken, long to, FacebookFeed facebookFeed) throws FacebookException {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(ACCESS_TOKEN, accessToken.value()));
        for (Map.Entry<String, String> entry : facebookFeed.asMap().entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        checkErrorOn(post("https", "graph.facebook.com", "/" + to + "/feed", null, nvps));
    }

    @Override
    public String buildOAuthURL(ClientType clientType, String... scopes) {
        String authorizationUrl = "https://www.facebook.com/dialog/oauth?client_id=" + clientId + "&redirect_uri=" + redirectUri;
        List<String> lstScopes = Arrays.asList(scopes);
        if (!lstScopes.isEmpty()) {
            String _scopes = "";
            for (Iterator<String> it = lstScopes.iterator(); it.hasNext(); ) {
                _scopes += it.next();
                if (it.hasNext()) {
                    _scopes += ",";
                }
            }
            authorizationUrl += "&scope=" + _scopes;
        }
        if (clientType == ClientType.MOBILE) {
            authorizationUrl = authorizationUrl + "&display=touch";
        }
        return authorizationUrl;
    }

    private void checkErrorOn(JSONType result) throws FacebookException {
        /*
 {
   "error": {
      "type": "OAuthException",
      "message": "Invalid OAuth access token signature."
   }
}
        */
        if (result.isObject() && result.asObject().has("error")) {
            throw new FacebookException(result.asObject().getObject("error").optString("message", "<no-details>"));
        }
    }

    private JSONType get(String proto, String host, String path, List<NameValuePair> qparams) {
        try {
            URI uri = URIUtils.createURI(proto, host, -1, path, qparams == null ? null : URLEncodedUtils.format(qparams, "UTF-8"), null);
            HttpResponse response = httpClient().execute(new HttpGet(uri));
            String contentType = response.getEntity().getContentType().getValue();
            contentType = contentType == null ? "" : contentType.toLowerCase();
            if (contentType.startsWith("application/json") || contentType.startsWith("text/javascript")) {
                return JSON.parse(EntityUtils.toString(response.getEntity()));
            } else {
                return JSON.object().put("data", EntityUtils.toString(response.getEntity()));
            }
        } catch (Exception e) {
            throw new FacebookException(e.getMessage(), e);
        }
    }

    private JSONType post(String proto, String host, String path, List<NameValuePair> qparams, List<NameValuePair> pparams) {
        try {
            URI uri = URIUtils.createURI(proto, host, -1, path, qparams == null ? null : URLEncodedUtils.format(qparams, "UTF-8"), null);
            HttpPost post = new HttpPost(uri);
            if (pparams != null) {
                post.setEntity(new UrlEncodedFormEntity(pparams, HTTP.UTF_8));
            }
            HttpResponse response = httpClient().execute(post);
            String contentType = response.getEntity().getContentType().getValue();
            contentType = contentType == null ? "" : contentType.toLowerCase();
            if (contentType.startsWith("application/json") || contentType.startsWith("text/javascript")) {
                return JSON.parse(EntityUtils.toString(response.getEntity()));
            } else {
                return JSON.object().put("data", EntityUtils.toString(response.getEntity()));
            }
        } catch (Exception e) {
            throw new FacebookException(e.getMessage(), e);
        }
    }

    private HttpClient httpClient() {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
        DefaultHttpClient client = new DefaultHttpClient(params);
        ClientConnectionManager ccm = client.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, sslSocketFactory));
        return client;
    }
}
