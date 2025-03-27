package com.mycompany.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class LoadPropertiesFIle {

    /**
     * Load application.properties file
     * @return property instance
     */
    private Properties getProperties() {

        Properties properties = new Properties();
        try {
            
            String propFile = "env.properties";

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFile);
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFile + "' not found in the classpath");
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return properties;
    }

    /**
     * Get the OIDC provider URL
     * @return URL string value from the properties
     */
    public String getOidcProviderURL() {
        System.out.println(this.getProperties().getProperty("oidcprovider.url"));
        return this.getProperties().getProperty("oidcprovider.url");
    }

    /**
     * Get the callback URL from the properties
     * @return callback URL String value from the properties
     */
    public String getCallBackURL() {
        return this.getProperties().getProperty("oidcprovider.redirect_uri");
    }

    /**
     * Get the client ID from the properties
     * @return cleint ID as known by the OIDC provider
     */
    public String getClientID() {
        return this.getProperties().getProperty("oidcprovider.client_id");
    }


    /**
     * Get the client Secret from the properties
     * @return cleint secret as known by the OIDC provider
     */
    public String getClientSecret() {
        return this.getProperties().getProperty("oidcprovider.client_secret");
    }

}
