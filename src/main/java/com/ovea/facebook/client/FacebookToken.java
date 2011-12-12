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
