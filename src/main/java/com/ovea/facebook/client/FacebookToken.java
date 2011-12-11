package com.ovea.facebook.client;

import java.util.Date;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class FacebookToken {

    private final String token;
    private final long expiresAt;
    private final int expiration;

    public FacebookToken(String token, int validityInSeconds) {
        if (token == null || token.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }
        if (validityInSeconds < 0) {
            throw new IllegalArgumentException("Expiration: " + validityInSeconds);
        }
        this.token = token.trim();
        this.expiresAt = System.currentTimeMillis() + validityInSeconds * 1000;
        this.expiration = validityInSeconds;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public int expiration() {
        return expiration;
    }

    public String value() {
        return token;
    }

    @Override
    public String toString() {
        return value() + " (expires at " + new Date(expiresAt) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacebookToken that = (FacebookToken) o;
        return token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }
}
