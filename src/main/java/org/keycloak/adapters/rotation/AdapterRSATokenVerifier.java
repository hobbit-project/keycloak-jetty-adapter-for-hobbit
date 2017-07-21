/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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

package org.keycloak.adapters.rotation;

import java.security.PublicKey;

import org.jboss.logging.Logger;
import org.keycloak.RSATokenVerifier;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 * 
 *         Adapted by Michael Roeder for the HOBBIT project, where a verifier
 *         without a realm check is needed.
 */
public class AdapterRSATokenVerifier {

    private static final Logger log = Logger.getLogger(AdapterRSATokenVerifier.class);

    public static final String CHECK_REALM_URL_KEY = "CHECK_REALM_URL";
    protected static final boolean CHECK_REALM_URL = getCheckRealmFlag();

    public static AccessToken verifyToken(String tokenString, KeycloakDeployment deployment)
            throws VerificationException {
        return verifyToken(tokenString, deployment, true, true);
    }

    private static boolean getCheckRealmFlag() {
        if (System.getenv().containsKey(CHECK_REALM_URL_KEY)) {
            try {
                return Boolean.parseBoolean(System.getenv().get(CHECK_REALM_URL_KEY));
            } catch (Exception e) {
                log.error(
                        "Couldn't parse the value of the CHECK_REALM_URL flag loaded from the environment. Setting it to true.");
                return true;
            }
        } else {
            log.warn("Couldn't get the CHECK_REALM_URL flag from the environment. Setting it to true.");
            return true;
        }
    }

    public static PublicKey getPublicKey(String kid, KeycloakDeployment deployment) throws VerificationException {
        PublicKeyLocator pkLocator = deployment.getPublicKeyLocator();

        PublicKey publicKey = pkLocator.getPublicKey(kid, deployment);
        if (publicKey == null) {
            log.errorf("Didn't find publicKey for kid: %s", kid);
            throw new VerificationException("Didn't find publicKey for specified kid");
        }

        return publicKey;
    }

    public static AccessToken verifyToken(String tokenString, KeycloakDeployment deployment, boolean checkActive,
            boolean checkTokenType) throws VerificationException {
        RSATokenVerifier verifier = RSATokenVerifier.create(tokenString).realmUrl(deployment.getRealmInfoUrl())
                .checkActive(checkActive).checkTokenType(checkTokenType);
        // Difference to the original class is the following line
        verifier.checkRealmUrl(CHECK_REALM_URL);
        PublicKey publicKey = getPublicKey(verifier.getHeader().getKeyId(), deployment);
        return verifier.publicKey(publicKey).verify().getToken();
    }
}
