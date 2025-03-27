### Connect2id test

This repo is aimed at testing the OIDC SDK from connect2id. It uses Servlet to set up 

#### Config
depencies used for the project:
```
    <!-- Dependency for managing OIDC connect -->
    <dependency>
      <groupId>com.nimbusds</groupId>
      <artifactId>oauth2-oidc-sdk</artifactId>
      <version>11.23.1</version>
    </dependency>
    <!-- depencency for singing and validating tokens -->
    <dependency>
      <groupId>com.nimbusds</groupId>
      <artifactId>nimbus-jose-jwt</artifactId>
      <version>10.0.2</version>
    </dependency>
```

If you use vscode, just open the project inside the .devcontainer, this will prevent you from changing your local machine configurations.
Otherwise, you will have either to configure the POM file to suit the java version on your machine, or change your java version to java 17.

Note that changing the java version migth also affect the servlet, and you will an OIDC provider (Auth0 was used for this project).

#### Run the application
Once you openned the project, do the following:
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