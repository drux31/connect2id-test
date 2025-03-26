### Connect2id test

This repo is aimed at testing the OIDC SDK from connect2id.

In order to run the project, after you cloned the repo do the following:
* create a `env.properties` file in `my-app/src/main/resources` ;
* add the following values with your corresponding config:
    ```
    oidcprovider.url=provider.url.com (could be google, Auth0, ADFS, Keycloak, ....)
    oidcprovider.client_id=client_id_as_known_by_provider
    oidcprovider.redirect_uri=http://localhost:3000/callback (replace by your actual callback)
    ```

* run the project:
    * `cd my-app`
    * `mvn jetty:run -Djetty.port=3000` (you can skip the port part if your default port is 8080)