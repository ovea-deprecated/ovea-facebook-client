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
