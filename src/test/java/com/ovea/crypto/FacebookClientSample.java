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
package com.ovea.crypto;

import com.ovea.facebook.client.DefaultFacebookClient;
import com.ovea.facebook.client.FacebookClient;
import com.ovea.facebook.client.FacebookException;
import com.ovea.facebook.client.FacebookToken;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
final class FacebookClientSample {
    public static void main(String[] args) throws FacebookException {
        FacebookClient client = new DefaultFacebookClient(
            "116942358380214",
            "c2b9b7c810289a893c6a31893ce18c08",
            "https://security.jaxspot.com/service/facebook/callback"
        );
        String credentials = "AQChTssaJqLjOm3qV2UqONjoilS4fmbCg4JLx7I0v-5GMyjwO1qhPxm_db6n0BLERMLsw9gRNd1NG2YjzYDMuqpGWg9GWNUbL2O6Tbc7cejpucblVZND-pCIMcU6wyQor_tfT-vtD8RN5Z6qEvuyUe3L-fQktoyv5_tLwkhVxS5w3BYCvSL07plhHhpRl0olrgg";
        FacebookToken token = client.accessToken(credentials);
        System.out.println(token);
        System.out.println(token);
        System.out.println(client.me(token));
        System.out.println(client.friends(token));
        /*client.publish(token, 123, new FacebookFeed()
            .message("message")
            .picture("http://www.les-calories.com/IMG/MwEs.jpg")
            .caption("caption")
            .description("description")
            .link("http://www.mcdonalds.com/")
            .name("name"));*/
    }
}
