## Keycloak-Jetty-adapter for HOBBIT

This projects comprises a fix that adapts the Keycloak-Jetty-adapter to the requirements of the HOBBIT project. In this project, it needs to be possible to turn of the realm checking if necessary.

### Build

1. Download https://downloads.jboss.org/keycloak/2.4.0.Final/adapters/keycloak-oidc/keycloak-jetty93-adapter-dist-2.4.0.Final.zip
1. Extract the zip file
1. Open `keycloak-adapter-core-2.4.0.Final.jar`
1. Delete `org.keycloak.adapters.rotation.AdapterRSATokenVerifier`
1. Build this project by running `mvn clean package`
1. Add the created `keycloak-jetty-adapter-for-hobbit-2.4.0.Final.jar` file to the `lib` folder of the extracted zip file
1. Create a new zip file with an adapted name, e.g., `keycloak-jetty93-adapter-for-hobbit-dist-2.4.0.Final.zip`

### Usage

The adapter can be used with Jetty as the original Keycloak-adapter. The difference is that when it is started, it will try to read the environmental variable `CHECK_REALM_URL`. If it is set to `false`, the realm check won't be executed.

### Implementation

Our implementation is based on version `2.4.0.FINAL` adapter provided by the [Keycloak project](https://github.com/keycloak/keycloak) and simply overrides the [`org.keycloak.adapters.rotation.AdapterRSATokenVerifier`](https://github.com/keycloak/keycloak/blob/2.4.0.Final/adapters/oidc/adapter-core/src/main/java/org/keycloak/adapters/rotation/AdapterRSATokenVerifier.java) class and sets the `checkRealmUrl` attribute of the [`org.keycloak.RSATokenVerifier`](https://github.com/keycloak/keycloak/blob/2.4.0.Final/core/src/main/java/org/keycloak/RSATokenVerifier.java) class