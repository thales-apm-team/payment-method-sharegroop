package com.payline.payment.sharegroop.utils.http;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.bean.payment.Data;
import com.payline.payment.sharegroop.exception.InvalidDataException;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.util.HashMap;

import static com.payline.payment.sharegroop.utils.http.HttpTestUtils.mockHttpResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SharegroopHttpClientTest {

    @InjectMocks
    @Spy
    private SharegroopHttpClient sharegroopHttpClient;
    @Mock
    private CloseableHttpClient http;

    @BeforeEach
    void setup() throws NoSuchFieldException {

        // Init tested instance and inject mocks
        sharegroopHttpClient = new SharegroopHttpClient();
        MockitoAnnotations.initMocks(this);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    // --- Test SharegroopHttpClient#execute ---
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void execute_nominal() throws IOException {
        // given: a properly formatted request, which gets a proper response
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        int expectedStatusCode = 200;
        String expectedStatusMessage = "OK";
        String expectedContent = "{\"content\":\"fake\"}";
        doReturn( mockHttpResponse( expectedStatusCode, expectedStatusMessage, expectedContent, null ) )
                .when( http ).execute( request );

        // when: sending the request
        StringResponse stringResponse = sharegroopHttpClient.execute( request );

        // then: the content of the StringResponse reflects the content of the HTTP response
        assertNotNull( stringResponse );
        assertEquals( expectedStatusCode, stringResponse.getStatusCode() );
        assertEquals( expectedStatusMessage, stringResponse.getStatusMessage() );
        assertEquals( expectedContent, stringResponse.getContent() );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void execute_retry() throws IOException {
        // given: the first 2 requests end up in timeout, the third request gets a response
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        when( http.execute( request ) )
                .thenThrow( ConnectTimeoutException.class )
                .thenThrow( ConnectTimeoutException.class )
                .thenReturn( mockHttpResponse( 200, "OK", "content", null) );

        // when: sending the request
        StringResponse stringResponse = sharegroopHttpClient.execute( request );

        // then: the client finally gets the response
        assertNotNull( stringResponse );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void execute_retryFail() throws IOException {
        // given: a request which always gets an exception
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        doThrow( IOException.class ).when( http ).execute( request );

        // when: sending the request, a PluginException is thrown
        assertThrows( PluginException.class, () -> sharegroopHttpClient.execute( request ) );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void execute_invalidResponse() throws IOException {
        // given: a request that gets an invalid response (null)
        HttpGet request = new HttpGet("http://domain.test.fr/malfunctioning-endpoint");
        doReturn( null ).when( http ).execute( request );

        // when: sending the request, a PluginException is thrown
        assertThrows( PluginException.class, () -> sharegroopHttpClient.execute( request ) );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    // --- Test SharegroopHttpClient#CreateOrder ---
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void createOrder_nominal() throws IOException {
        // given: Valid parameter  to create a request configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfigurationWithCollect(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());
        // TODO: N'ajouter dans les classes utilitaires que le code utilisé à plusieurs endroits
        doReturn(MockUtils.createOrderValidResponse()).when( sharegroopHttpClient ).execute( any(HttpRequestBase.class ));

        // when : calling verifyConnection method
        Data result = sharegroopHttpClient.createOrder(requestConfiguration,MockUtils.anOrder());

        // then
        assertNotNull(result);
        // TODO: prendre l'habitude de vérifier que les mocks sont bien appliqués (en particulier dans les cas où on attend une erreur)
        verify( http, never() ).execute( any(HttpRequestBase.class) );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void createOrder_missingApiUrl(){
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfigurationWithCollect(), MockUtils.anEnvironment(), new PartnerConfiguration( new HashMap<>(), new HashMap<>() ) );

        // when calling the paymentInit method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.createOrder(requestConfiguration,MockUtils.anOrder()));
    }
    /**------------------------------------------------------------------------------------------------------------------*/
@Test
    void createOrder_invalidApiUrl(){
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfigurationWithCollect(), MockUtils.anEnvironment(), MockUtils.aInvalidPartnerConfiguration());

        // when calling the paymentInit method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.createOrder(requestConfiguration,MockUtils.anOrder()));
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    // --- Test SharegroopHttpClient#VerifyConnection ---
    /**------------------------------------------------------------------------------------------------------------------*/


    @Test
    void verifyPrivateKey_nominal(){
        // given: Valid parameter  to create a request configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfigurationWithCollect(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());


        doReturn(MockUtils.verifyPrivateKeyValidResponse()).when( sharegroopHttpClient ).execute( any(HttpRequestBase.class ));

        // when : calling verifyConnection method
        Boolean result = sharegroopHttpClient.verifyPrivateKey(requestConfiguration);

        // then
        assertTrue(result);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void verifyPrivateKey_missingApiUrl(){
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfigurationWithCollect(), MockUtils.anEnvironment(), new PartnerConfiguration( new HashMap<>(), new HashMap<>() ) );

        // when calling the paymentInit method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyPrivateKey(requestConfiguration));
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void verifyPrivateKey_invalidApiUrl(){
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfigurationWithCollect(), MockUtils.anEnvironment(), MockUtils.aInvalidPartnerConfiguration());

        // when calling the paymentInit method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyPrivateKey(requestConfiguration));
    }
    /**------------------------------------------------------------------------------------------------------------------*/


}
