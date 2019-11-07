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
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

        String content = "{\"success\":true," +
                "\"data\":{" +
                "\"id\":\"ord_92aa7cfc-45df-4cf8-96f9-dd19b4bb3d09\"," +
                "\"platformId\":\"pl_5ee79772-d68b-4e83-b334-b9b5c0349738\"," +
                "\"amount\":10000," +
                "\"amountConfirmed\":0," +
                "\"delay\":8640," +
                "\"secure3D\":true," +
                "\"currency\":\"EUR\"," +
                "\"locale\":\"en\"," +
                "\"ux\":\"collect\"," +
                "\"type\":\"direct\"," +
                "\"status\":\"initiated\"," +
                "\"createdAt\":1572886069520," +
                "\"email\":\"captain@example.com\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Carter\"," +
                "\"trackId\":\"MY-INTERN-ID\"," +
                "\"items\":[{" +
                "\"id\":\"itm_e030779a-e943-46c4-95d8-0436bd678cd8\"," +
                "\"name\":\"Product A\"," +
                "\"amount\":2200," +
                "\"quantity\":1," +
                "\"trackId\":\"MY-ITEM-ID\"," +
                "\"description\":\"Description A\"}," +
                "{\"id\":\"itm_803cadd2-2bdd-4664-a926-571bd5c9314e\"," +
                "\"name\":\"Product B\"," +
                "\"amount\":5000," +
                "\"quantity\":1," +
                "\"description\":\"Description B\"}," +
                "{\"id\":\"itm_3eedca1c-622b-4cd3-b0b5-64d45a22d3a4\"," +
                "\"name\":\"Product C\"," +
                "\"amount\":2800," +
                "\"quantity\":1}]," +
                "\"dueDate\":1573404469576}}";

        StringResponse response =  HttpTestUtils.mockStringResponse(200, "OK", content, null);


        doReturn(response).when( sharegroopHttpClient ).execute( any(HttpRequestBase.class ));

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
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), new PartnerConfiguration( new HashMap<>(), new HashMap<>() ) );

        // when calling the paymentInit method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.createOrder(requestConfiguration,MockUtils.anOrder()));
    }
    /**------------------------------------------------------------------------------------------------------------------*/
@Test
    void createOrder_invalidApiUrl(){
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aInvalidPartnerConfiguration());

        // when calling the paymentInit method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.createOrder(requestConfiguration,MockUtils.anOrder()));
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    // --- Test SharegroopHttpClient#VerifyConnection ---
    /**------------------------------------------------------------------------------------------------------------------*/


    @Test
    void verifyPrivateKey_nominal(){
        // given: Valid parameter  to create a request configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

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
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), new PartnerConfiguration( new HashMap<>(), new HashMap<>() ) );

        // when calling the paymentInit method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyPrivateKey(requestConfiguration));
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void verifyPrivateKey_invalidApiUrl(){
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration( MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aInvalidPartnerConfiguration());

        // when calling the paymentInit method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyPrivateKey(requestConfiguration));
    }
    /**------------------------------------------------------------------------------------------------------------------*/


}
