package com.payline.payment.sharegroop.utils.http;

import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.bean.payment.Data;
import com.payline.payment.sharegroop.bean.payment.Order;
import com.payline.payment.sharegroop.exception.InvalidDataException;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.PluginUtils;
import com.payline.payment.sharegroop.utils.properties.ConfigProperties;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class SharegroopHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(SharegroopHttpClient.class);
    private ConfigProperties config = ConfigProperties.getInstance();

    //Headers
    private static final String CONTENT_TYPE_VALUE = "application/json";

    // Paths
    public static final String PATH_VERSION = "v1";
    public static final String PATH_ORDER = "orders";
    public static final String REFUND = "refund";
    public static final String CANCEL = "cancel";

    // Exceptions messages
    private static final String SERVICE_URL_ERROR = "Service URL is invalid";
    private static final String MISSING_ORDER_ID = "Missing an order Id";

    /**
     * The number of time the client must retry to send the request if it doesn't obtain a response.
     */
    private int retries;

    private HttpClient client;

    // --- Singleton Holder pattern + initialization BEGIN
    /* TODO: on l'a déjà vu ensemble, cette variable initialized ne sert à rien dans ces conditions
    (on ne passe jamais 2 fois dans le constructeur). A supprimer.
     */
    private AtomicBoolean initialized = new AtomicBoolean();

    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    SharegroopHttpClient() {
        if (this.initialized.compareAndSet(false, true)) {
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
            } catch (NumberFormatException e) {
                throw new PluginException("plugin error: http.* properties must be integers", e);
            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                    .setConnectTimeout(connectTimeout * 1000)
                    .setSocketTimeout(socketTimeout * 1000)
                    .build();

            // instantiate Apache HTTP client
            this.client = HttpClientBuilder.create()
                    .useSystemProperties()
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        }
    }
    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    private static class Holder {
        private static final SharegroopHttpClient instance = new SharegroopHttpClient();
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    public static SharegroopHttpClient getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END

    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
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
    StringResponse execute(HttpRequestBase httpRequest) {
        StringResponse strResponse = null;
        int attempts = 1;

        while (strResponse == null && attempts <= this.retries) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Start call to partner API (attempt {}) :" + System.lineSeparator() + PluginUtils.requestToString(httpRequest), attempts);
            } else {
                LOGGER.info("Start call to partner API [{} {}] (attempt {})", httpRequest.getMethod(), httpRequest.getURI(), attempts);
            }
            try (CloseableHttpResponse httpResponse = (CloseableHttpResponse) this.client.execute(httpRequest)) {
                strResponse = StringResponse.fromHttpResponse(httpResponse);
            } catch (IOException e) {
                LOGGER.error("An error occurred during the HTTP call :", e);
                strResponse = null;
            } finally {
                attempts++;
            }
        }

        if (strResponse == null) {
            throw new PluginException("Failed to contact the partner API", FailureCause.COMMUNICATION_ERROR);
        }
        LOGGER.info("Response obtained from partner API [{} {}]", strResponse.getStatusCode(), strResponse.getStatusMessage());
        return strResponse;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Verify if API url are present
     *
     * @param requestConfiguration
     */
    public void verifyPartnerConfiguartionURL(RequestConfiguration requestConfiguration) {
        if (requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.SHAREGROOP_URL)== null) {
            throw new InvalidDataException("Missing API url from partner configuration (sentitive properties)");
        }

        // TODO: on aurait pu mettre un "OU" entre les 2 conditions ci-dessous pour s'éviter un copier-coller de message
        if (requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PRIVATE_KEY) == null) {
            throw new InvalidDataException("Missing client private key from partner configuration (sentitive properties)");
        }

        if (requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PRIVATE_KEY).getValue() == null) {
            throw new InvalidDataException("Missing client private key from partner configuration (sentitive properties)");
        }
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Verify if the private key is valid
     *
     * @param requestConfiguration
     * @return
     */
    public Boolean verifyPrivateKey(RequestConfiguration requestConfiguration) {

        // Check if API url are present
        verifyPartnerConfiguartionURL(requestConfiguration);

        String baseUrl = requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.SHAREGROOP_URL);

        // Init request
        URI uri;

        try {
            uri = new URI(baseUrl + createPath(PATH_VERSION, PATH_ORDER));
        } catch (URISyntaxException e) {
            throw new InvalidDataException(SERVICE_URL_ERROR, e);
        }

        HttpPost httpPost = new HttpPost(uri);

        String privateKeyHolder = requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PRIVATE_KEY).getValue();

        httpPost.setHeader(HttpHeaders.AUTHORIZATION, privateKeyHolder);

        // Execute request
        StringResponse response = this.execute(httpPost);

        return response.getContent().contains("{\"status\":400,\"success\":false,\"errors\":[\"should be object\"]}");
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Create a transaction
     *
     * @param requestConfiguration
     * @return
     */
    public Data createOrder(RequestConfiguration requestConfiguration, Order order) {
        // Check if API url are present
        verifyPartnerConfiguartionURL(requestConfiguration);

        String baseUrl = requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.SHAREGROOP_URL);

        // Init request
        URI uri;

        try {
            uri = new URI(baseUrl + createPath(PATH_VERSION, PATH_ORDER));
        } catch (URISyntaxException e) {
            throw new InvalidDataException(SERVICE_URL_ERROR, e);
        }

        HttpPost httpPost = new HttpPost(uri);

        // Headers
        String privateKeyHolder = requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PRIVATE_KEY).getValue();

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, privateKeyHolder);
        headers.put(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_VALUE);

        for (Map.Entry<String, String> h : headers.entrySet()) {
            httpPost.setHeader(h.getKey(), h.getValue());
        }

        // Body
        String jsonBody = order.toString();
        httpPost.setEntity( new StringEntity( jsonBody, StandardCharsets.UTF_8 ));

        // Execute request
        StringResponse response = this.execute(httpPost);

        return SharegroopAPICallResponse.fromJson(response.getContent()).getData();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Verify the transaction status after a buyer action
     * @param requestConfiguration
     * @param createdOrderId
     * @return
     */
    public Data verifyOrder(RequestConfiguration requestConfiguration, String createdOrderId){
        // Check if API url are present
        verifyPartnerConfiguartionURL(requestConfiguration);

        // Check if the createdOrderId is present
        if (createdOrderId == null) {
            throw new InvalidDataException(MISSING_ORDER_ID);
        }

        String baseUrl = requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.SHAREGROOP_URL);

        // Init request
        URI uri;

        try {
            // Add the createOrderId to the url
            uri = new URI(baseUrl + createPath(PATH_VERSION, PATH_ORDER, createdOrderId));
        } catch (URISyntaxException e) {
            throw new InvalidDataException(SERVICE_URL_ERROR, e);
        }

        HttpGet httpGet = new HttpGet(uri);

        // Headers
        String privateKeyHolder = requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PRIVATE_KEY).getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, privateKeyHolder);
        headers.put(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_VALUE);

        for (Map.Entry<String, String> h : headers.entrySet()) {
            httpGet.setHeader(h.getKey(), h.getValue());
        }

        // Execute request
        StringResponse response = this.execute(httpGet);

        return SharegroopAPICallResponse.fromJson(response.getContent()).getData();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Refund each participant
     * @param requestConfiguration
     * @return
     */
    public Data refundOrder(RequestConfiguration requestConfiguration, String createdOrderId){
        return post(requestConfiguration,createdOrderId,REFUND);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Cancel an incompleted transaction
     * @param requestConfiguration
     * @param createdOrderId
     * @return
     */
    public Data cancelOrder(RequestConfiguration requestConfiguration, String createdOrderId){
        return post(requestConfiguration,createdOrderId,CANCEL);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Manage an API call to cancel or refund a transaction
     * @param requestConfiguration
     * @param createdOrderId
     * @param path
     * @return
     */
    /* TODO: je pense qu'on a moyen de mutualiser pas mal de code entre les différents POST réalisés dans cette méthode (4)
    Mais ce n'est pas prioritaire.
     */
    public Data post(RequestConfiguration requestConfiguration, String createdOrderId, String path){
        // Check if API url are present
        verifyPartnerConfiguartionURL(requestConfiguration);

        String baseUrl = requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.SHAREGROOP_URL);

        // Init request
        URI uri;

        try {
            // Add the createOrderId to the url
            uri = new URI(baseUrl + createPath(PATH_VERSION, PATH_ORDER, createdOrderId,path));
        } catch (URISyntaxException e) {
            throw new InvalidDataException(SERVICE_URL_ERROR, e);
        }

        HttpPost httpPost = new HttpPost(uri);

        // Headers
        String privateKeyHolder = requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PRIVATE_KEY).getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, privateKeyHolder);
        headers.put(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_VALUE);

        for (Map.Entry<String, String> h : headers.entrySet()) {
            httpPost.setHeader(h.getKey(), h.getValue());
        }

        // Execute request
        StringResponse response = this.execute(httpPost);

        return SharegroopAPICallResponse.fromJson(response.getContent()).getData();
    }

}
