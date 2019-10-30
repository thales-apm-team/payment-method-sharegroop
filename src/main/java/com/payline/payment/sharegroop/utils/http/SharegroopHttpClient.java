package com.payline.payment.sharegroop.utils.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.sharegroop.bean.TemplateRequest;
import com.payline.payment.sharegroop.bean.payment.Orders;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.properties.ConfigProperties;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


public class SharegroopHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(SharegroopHttpClient.class);
    private ConfigProperties config = ConfigProperties.getInstance();
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String AUTHENTICATION_KEY = "Authorization";
    private static final String CONTENT_TYPE = "application/json";

    // TODO : Implemeter le système de retries ( 3 essais)

    private HttpClient client;
    private Gson parser;

    // --- Singleton Holder pattern + initialization BEGIN
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
    public void init(){

        // Initialise Json parser
        this.parser = new GsonBuilder().create();

        // Set request configuration
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2 * 1000)
                .setConnectionRequestTimeout(3 * 1000)
                .setSocketTimeout(4 * 1000).build();

        // Create Http client with the request configuration
        final HttpClientBuilder builder = HttpClientBuilder.create();
        builder.useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier()));

        this.client = builder.build();

    }
    // --- Singleton Holder pattern + initialization END
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Get the host URL
     * @return
     */
    public String getHost() {
        return Constants.PartnerConfigurationKeys.SHAREGROOP_URL;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private Header[] createHeaders(String authentication) {
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader(CONTENT_TYPE_KEY, CONTENT_TYPE);
        headers[0] = new BasicHeader(AUTHENTICATION_KEY, authentication);
        return headers;
    }
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
    public HttpResponse doGet(String scheme, String host, String path, Header[] headers) throws IOException, URISyntaxException {

        final URI uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path)
                .build();

        final HttpGet httpGetRequest = new HttpGet(uri);
        httpGetRequest.setHeaders(headers);
        return client.execute(httpGetRequest);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    public HttpResponse doPost(String scheme, String host, String path, Header[] headers, String body) throws IOException, URISyntaxException {

        final URI uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path)
                .build();

        final HttpPost httpPostRequest = new HttpPost(uri);
        httpPostRequest.setHeaders(headers);
        httpPostRequest.setEntity(new StringEntity(body));
        return client.execute(httpPostRequest);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    public Orders verifyConection() throws IOException, URISyntaxException {

        String host = getHost();


        String path =  createPath(Constants.PartnerConfigurationKeys.PATH_VERSION, Constants.PartnerConfigurationKeys.PATH_ORDER);

        String jsonBody = "";

        Header[] headers = createHeaders("sk_test_fb1ec051-4d3a-4f0e-a9dd-40141013e324 ");

        // do the request
        HttpResponse response = doPost(Constants.PartnerConfigurationKeys.SCHEME, host, path, headers, jsonBody);

        // create object from Template response
        String responseString = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);

        return parser.fromJson(responseString, Orders.class);
    }
    /**------------------------------------------------------------------------------------------------------------------*/

}
