package com.ovea.facebook.client;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class FacebookFeed {
    private final Map<String, String> items = new HashMap<String, String>();

    Map<String, String> asMap() {
        return items;
    }

    public FacebookFeed message(String message) {
        items.put("message", message);
        return this;
    }

    public FacebookFeed picture(String picture) {
        items.put("picture", picture);
        return this;
    }

    public FacebookFeed name(String name) {
        items.put("name", name);
        return this;
    }

    public FacebookFeed caption(String caption) {
        items.put("caption", caption);
        return this;
    }

    public FacebookFeed link(String link) {
        items.put("link", link);
        return this;
    }

    public FacebookFeed description(String description) {
        items.put("description", description);
        return this;
    }

}
