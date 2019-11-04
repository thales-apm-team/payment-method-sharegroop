package com.payline.payment.sharegroop.utils.http;

import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.exception.InvalidDataException;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.PluginUtils;
import com.payline.payment.sharegroop.utils.properties.ConfigProperties;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;



public class SharegroopHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(SharegroopHttpClient.class);
    private ConfigProperties config = ConfigProperties.getInstance();

    //Headers
    private static final String MISSING_API_URL_ERROR = "Missing API base url in partnerConfiguration";

    // Paths
    public static final String PATH_VERSION = "v1";
    public static final String PATH_ORDER = "orders";

    /**
     * The number of time the client must retry to send the request if it doesn't obtain a response.
     */
    private int retries;

    private HttpClient client;

    // --- Singleton Holder pattern + initialization BEGIN
    private AtomicBoolean initialized = new AtomicBoolean();
    /**------------------------------------------------------------------------------------------------------------------*/
    SharegroopHttpClient(){

    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private static class Holder {
        private static final SharegroopHttpClient instance = new SharegroopHttpClient();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    public static SharegroopHttpClient getInstance() {
        return Holder.instance;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    public void init( PartnerConfiguration partnerConfiguration ){
        if( this.initialized.compareAndSet(false, true) ){
            int connectionRequestTimeout;
            int connectTimeout;
            int socketTimeout;
            try {
                // request config timeouts (in seconds)
                connectionRequestTimeout = Integer.parseInt(config.get("http.connectionRequestTimeout"));
                connectTimeout = Integer.parseInt(config.get("http.connectTimeout"));
                socketTimeout = Integer.parseInt(config.get("http.socketTimeout"));

                // retries
                this.retries = Integer.parseInt(config.get("http.retries"));
            }
            catch( NumberFormatException e ){
                throw new PluginException("plugin error: http.* properties must be integers", e);
            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                    .setConnectTimeout(connectTimeout * 1000)
                    .setSocketTimeout(socketTimeout * 1000)
                    .build();

            if( partnerConfiguration.getProperty( Constants.PartnerConfigurationKeys.SHAREGROOP_URL_SANDBOX ) == null ) {
                throw new InvalidDataException("Missing SandBox API url from partner configuration (sentitive properties)");
            }

            if( partnerConfiguration.getProperty( Constants.PartnerConfigurationKeys.SHAREGROOP_URL ) == null ){
                throw new InvalidDataException("Missing API url from partner configuration (sentitive properties)");
            }

            // instantiate Apache HTTP client
            this.client = HttpClientBuilder.create()
                    .useSystemProperties()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        }
    }
    // --- Singleton Holder pattern + initialization END
    /**------------------------------------------------------------------------------------------------------------------*/
    public String createPath(String... path) {
        StringBuilder sb = new StringBuilder("/");
        if (path != null && path.length > 0) {
            for (String aPath : path) {
                sb.append(aPath).append("/");
            }
        }
        return sb.toString();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Send the request, with a retry system in case the client does not obtain a proper response from the server.
     *
     * @param httpRequest The request to send.
     * @return The response converted as a {@link StringResponse}.
     * @throws PluginException If an error repeatedly occurs and no proper response is obtained.
     */
    StringResponse execute( HttpRequestBase httpRequest ){
        StringResponse strResponse = null;
        int attempts = 1;

        while( strResponse == null && attempts <= this.retries ){
            if( LOGGER.isDebugEnabled() ){
                LOGGER.debug( "Start call to partner API (attempt {}) :" + System.lineSeparator() + PluginUtils.requestToString( httpRequest ), attempts );
            } else {
                LOGGER.info( "Start call to partner API [{} {}] (attempt {})", httpRequest.getMethod(), httpRequest.getURI(), attempts );
            }
            try( CloseableHttpResponse httpResponse = (CloseableHttpResponse) this.client.execute( httpRequest )){
                strResponse = StringResponse.fromHttpResponse( httpResponse );
            }
            catch (IOException e) {
                LOGGER.error("An error occurred during the HTTP call :", e);
                strResponse = null;
            }
            finally {
                attempts++;
            }
        }

        if( strResponse == null ){
            throw new PluginException( "Failed to contact the partner API", FailureCause.COMMUNICATION_ERROR );
        }
        LOGGER.info("Response obtained from partner API [{} {}]", strResponse.getStatusCode(), strResponse.getStatusMessage() );
        return strResponse;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    public Boolean verifyPrivateKey(RequestConfiguration requestConfiguration) {

        String baseUrl = requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.SHAREGROOP_URL_SANDBOX);
        if( baseUrl == null ){
            throw new InvalidDataException( MISSING_API_URL_ERROR );
        }

        // Init request
        URI uri;

        try {
            uri = new URI( baseUrl + createPath(PATH_VERSION, PATH_ORDER));
        } catch (URISyntaxException e) {
            throw new InvalidDataException("Service URL is invalid", e);
        }

        HttpPost httpPost = new HttpPost( uri );

        if( requestConfiguration.getContractConfiguration().getProperty( Constants.ContractConfigurationKeys.PRIVATE_KEY ).getValue() == null ){
            throw new InvalidDataException("Missing client private key from partner configuration (sentitive properties)");
        }

        String privateKeyHolder = requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PRIVATE_KEY).getValue();

        httpPost.setHeader(HttpHeaders.AUTHORIZATION, privateKeyHolder);

        // Execute request
        StringResponse response = this.execute( httpPost );

        if(response.getContent().contains("{\"status\":400,\"success\":false,\"errors\":[\"should be object\"]}")){
            return true;
        }

        return false;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
}
