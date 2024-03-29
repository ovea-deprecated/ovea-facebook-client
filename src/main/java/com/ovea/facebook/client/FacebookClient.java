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
