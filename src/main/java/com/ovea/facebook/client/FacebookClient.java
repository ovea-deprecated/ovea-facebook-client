package com.ovea.facebook.client;

import com.ovea.json.JSONArray;
import com.ovea.json.JSONObject;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public interface FacebookClient {
    FacebookToken accessToken(String verificationCode) throws FacebookException;

    JSONObject me(FacebookToken accessToken) throws FacebookException;

    JSONArray friends(FacebookToken accessToken) throws FacebookException;

    void publish(FacebookToken accessToken, long to, FacebookFeed facebookFeed) throws FacebookException;

    String buildOAuthURL(ClientType clientType, String... scopes);
}
