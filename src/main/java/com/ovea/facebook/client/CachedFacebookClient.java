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

import com.ovea.cache.*;
import com.ovea.json.JSONArray;
import com.ovea.json.JSONObject;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class CachedFacebookClient implements FacebookClient {

    private final FacebookClient delegate;
    private final Cache<FacebookToken> cache;

    @Inject
    public CachedFacebookClient(final FacebookClient delegate, MutableCache<FacebookToken> cache) {
        this.delegate = delegate;
        this.cache = new SelfProvidingCache<FacebookToken>(cache, new CacheEntryProvider<FacebookToken>() {
            @Override
            public CacheEntry<FacebookToken> get(String key) throws CacheException {
                try {
                    FacebookToken token = delegate.accessToken(key);
                    return new ExpiringCacheEntry<FacebookToken>(key, token, token.expiration(), TimeUnit.SECONDS);
                } catch (FacebookException e) {
                    throw new CacheException(e);
                }
            }
        });
    }

    @Override
    public FacebookToken accessToken(String verificationCode) throws FacebookException {
        try {
            return cache.get(verificationCode);
        } catch (CacheException e) {
            e.rethrowIf(FacebookException.class);
            throw e;
        }
    }

    @Override
    public String buildOAuthURL(ClientType clientType, String... scopes) {
        return delegate.buildOAuthURL(clientType, scopes);
    }

    @Override
    public JSONArray friends(FacebookToken accessToken) throws FacebookException {
        return delegate.friends(accessToken);
    }

    @Override
    public JSONObject me(FacebookToken accessToken) throws FacebookException {
        return delegate.me(accessToken);
    }

    @Override
    public void publish(FacebookToken accessToken, long to, FacebookFeed facebookFeed) throws FacebookException {
        delegate.publish(accessToken, to, facebookFeed);
    }
}
