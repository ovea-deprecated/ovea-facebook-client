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
