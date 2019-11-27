package com.payline.payment.sharegroop.utils.http;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.exception.InvalidDataException;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
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
import java.util.Map;

import static com.payline.payment.sharegroop.utils.http.HttpTestUtils.mockHttpResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SharegroopHttpClientTest {

    @InjectMocks
    @Spy
    private SharegroopHttpClient sharegroopHttpClient;
    @Mock
    private CloseableHttpClient http;

    @BeforeEach
    void setup() {
        // Init tested instance and inject mocks
        sharegroopHttpClient = new SharegroopHttpClient();
        MockitoAnnotations.initMocks(this);
    }
    // --- Test SharegroopHttpClient#execute ---

    @Test
    void execute_nominal() throws IOException {
        // given: a properly formatted request, which gets a proper response
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        int expectedStatusCode = 200;
        String expectedStatusMessage = "OK";
        String expectedContent = "{\"content\":\"fake\"}";
        doReturn(mockHttpResponse(expectedStatusCode, expectedStatusMessage, expectedContent, null))
                .when(http).execute(request);

        // when: sending the request
        StringResponse stringResponse = sharegroopHttpClient.execute(request);

        // then: the content of the StringResponse reflects the content of the HTTP response
        assertNotNull(stringResponse);
        assertEquals(expectedStatusCode, stringResponse.getStatusCode());
        assertEquals(expectedStatusMessage, stringResponse.getStatusMessage());
        assertEquals(expectedContent, stringResponse.getContent());
    }

    @Test
    void execute_retry() throws IOException {
        // given: the first 2 requests end up in timeout, the third request gets a response
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        when(http.execute(request))
                .thenThrow(ConnectTimeoutException.class)
                .thenThrow(ConnectTimeoutException.class)
                .thenReturn(mockHttpResponse(200, "OK", "content", null));

        // when: sending the request
        StringResponse stringResponse = sharegroopHttpClient.execute(request);

        // then: the client finally gets the response
        assertNotNull(stringResponse);
    }

    @Test
    void execute_retryFail() throws IOException {
        // given: a request which always gets an exception
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        doThrow(IOException.class).when(http).execute(request);

        // when: sending the request, a PluginException is thrown
        assertThrows(PluginException.class, () -> sharegroopHttpClient.execute(request));
    }

    @Test
    void execute_invalidResponse() throws IOException {
        // given: a request that gets an invalid response (null)
        HttpGet request = new HttpGet("http://domain.test.fr/malfunctioning-endpoint");
        doReturn(null).when(http).execute(request);

        // when: sending the request, a PluginException is thrown
        assertThrows(PluginException.class, () -> sharegroopHttpClient.execute(request));
    }
    // --- Test SharegroopHttpClient#CreateOrder ---

    @Test
    void createOrder_nominal() throws IOException {
        // given: Valid parameter  to create a request configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

        String content = MockUtils.aShareGroopResponse("initiated");
        StringResponse response = HttpTestUtils.mockStringResponse(200, "OK", content, null);
        doReturn(response).when(sharegroopHttpClient).execute(any(HttpRequestBase.class));

        // when : calling createOrder method
        SharegroopAPICallResponse result = sharegroopHttpClient.createOrder(requestConfiguration, MockUtils.anOrder());

        // then
        assertNotNull(result);
        verify(http, never()).execute(any(HttpRequestBase.class));
    }

    @Test
    void createOrder_missingApiUrl() {
        PartnerConfiguration partnerConfiguration = MockUtils.aPartnerConfiguration();
        partnerConfiguration.getSensitiveProperties().put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "https://api.sandbox.sharegroop.com");

        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), new PartnerConfiguration(new HashMap<>(), new HashMap<>()));

        // when calling the createOrder method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.createOrder(requestConfiguration, MockUtils.anOrder()));

    }

    @Test
    void createOrder_invalidApiUrl() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the createOrder method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.createOrder(requestConfiguration, MockUtils.anOrder()));
    }

    @Test
    void createOrder_invalidPrivateKey() {

        // given: the Private Key is missing from the contractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty(null));

        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the createOrder method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.createOrder(requestConfiguration, MockUtils.anOrder()));
    }

    @Test
    void createOrder_missingPrivateKey() {

        // given: the Private Key is missing from the contractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, null);

        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the createOrder method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.createOrder(requestConfiguration, MockUtils.anOrder()));
    }
    // --- Test SharegroopHttpClient#VerifyPrivatekey ---

    @Test
    void verifyPrivateKey_nominal() {
        // given: Valid parameter  to create a request configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());
        StringResponse verifyPrivateKeyValidResponse = HttpTestUtils.mockStringResponse(400, "Bad Request", "{\"status\":400,\"success\":false,\"errors\":[\"should be object\"]}", null);

        doReturn(verifyPrivateKeyValidResponse).when(sharegroopHttpClient).execute(any(HttpRequestBase.class));

        // when : calling verifyPrivateKey method
        Boolean result = sharegroopHttpClient.verifyPrivateKey(requestConfiguration);

        // then
        assertTrue(result);
    }

    @Test
    void verifyPrivateKey_missingApiUrl() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), new PartnerConfiguration(new HashMap<>(), new HashMap<>()));

        // when calling the verifyPrivateKey method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyPrivateKey(requestConfiguration));
    }

    @Test
    void verifyPrivateKey_invalidApiUrl() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the verifyPrivateKey method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyPrivateKey(requestConfiguration));
    }

    @Test
    void verifyPrivateKey_invalidPrivateKey() {

        // given: the Private Key is invalid in the contractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty(null));

        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the verifyPrivateKey method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyPrivateKey(requestConfiguration));
    }

    @Test
    void verifyPrivateKey_missingPrivateKey() {

        // given: the Private Key is missing from the contractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, null);

        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the verifyPrivateKey method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyPrivateKey(requestConfiguration));
    }

    // --- Test SharegroopHttpClient#Verify ---

    @Test
    void verifyOrder_missingApiUrl() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), new PartnerConfiguration(new HashMap<>(), new HashMap<>()));

        // when calling the verify method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyOrder(requestConfiguration, MockUtils.anOrderId()));
    }

    @Test
    void verifyOrder_invalidApiUrl() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the verify method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyOrder(requestConfiguration, MockUtils.anOrderId()));
    }

    @Test
    void verifyOrder_invalidOrderId() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the verify method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyOrder(requestConfiguration, null));
    }

    @Test
    void verifyOrder_nominal() throws IOException {
        // given: Valid parameter  to create a request configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

        String content = MockUtils.aShareGroopResponse("initiated");
        StringResponse response = HttpTestUtils.mockStringResponse(200, "OK", content, null);
        doReturn(response).when(sharegroopHttpClient).execute(any(HttpRequestBase.class));

        // when : calling verify method
        SharegroopAPICallResponse result = sharegroopHttpClient.verifyOrder(requestConfiguration, MockUtils.anOrderId());

        // then
        assertNotNull(result);

        verify(http, never()).execute(any(HttpRequestBase.class));
    }

    @Test
    void verifyOrder_invalidPrivateKey() {

        // given: the Private Key is invalid in the contractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty(null));

        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the createOrder method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyOrder(requestConfiguration, MockUtils.anOrderId()));
    }

    @Test
    void verifyOrder_missingPrivateKey() {

        // given: the Private Key is missing from the contractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, null);

        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the verifyOrder method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.verifyOrder(requestConfiguration, MockUtils.anOrderId()));
    }

    // --- Test SharegroopHttpClient#Refund ---
    @Test
    void refundOrder_nominal() throws IOException {
        // given: Valid parameter  to create a request configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

        String content = "{\"success\":true,\"data\":{\"currency\":\"EUR\",\"lastName\":\"Doe\",\"platformId\":\"pl_5ee79772-d68b-4e83-b334-b9b5c0349738\",\"delay\":8640,\"dueDate\":1573737727739,\"status\":\"refunded\",\"email\":\"martin@email.com\",\"firstName\":\"John\",\"id\":\"ord_7d4ca1a9-1c4e-47bd-9d1a-9330b605571d\",\"toProcess\":1,\"ux\":\"collect\",\"ecard\":false,\"locale\":\"en\",\"trackId\":\"TRACK-1\",\"createdAt\":1573219327739,\"integration\":\"front\",\"items\":[{\"name\":\"Product A\",\"description\":\"Description A\",\"amount\":12000,\"id\":\"itm_9de81228-7034-4f17-a07a-c85b8da98cea\",\"quantity\":1,\"trackId\":\"TRACK-A\"}],\"amountConfirmed\":12000,\"updatedAt\":1573219550511,\"nbShares\":1,\"amount\":12000,\"secure3D\":true,\"type\":\"direct\"}}";

        StringResponse response = HttpTestUtils.mockStringResponse(200, "OK", content, null);

        doReturn(response).when(sharegroopHttpClient).execute(any(HttpRequestBase.class));

        // when : calling refund method
        SharegroopAPICallResponse result = sharegroopHttpClient.refundOrder(requestConfiguration, MockUtils.anOrderId());

        // then
        assertNotNull(result);

        verify(http, never()).execute(any(HttpRequestBase.class));
    }

    // --- Test SharegroopHttpClient#Cancel ---

    @Test
    void post_missingApiUrl() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), new PartnerConfiguration(new HashMap<>(), new HashMap<>()));

        // when calling the refund method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.post(requestConfiguration, MockUtils.anOrderId(), "CANCEL", null));
    }

    @Test
    void post_invalidApiUrl() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the refund method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.post(requestConfiguration, MockUtils.anOrderId(), "CANCEL", null));
    }

    @Test
    void post_invalidOrderId() {
        // given: the API base URL is missing from the partner configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the refund method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.post(requestConfiguration, MockUtils.anOrderId(), "CANCEL", null));
    }

    @Test
    void post_invalidPrivateKey() {

        // given: the Private Key is invalid in the contractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty(null));

        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the refundOrder method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.post(requestConfiguration, MockUtils.anOrderId(), "CANCEL", null));
    }

    @Test
    void post_missingPrivateKey() {

        // given: the Private Key is missing from the contractConfiguration
        ContractConfiguration contractConfiguration = MockUtils.aContractConfiguration();
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, null);

        RequestConfiguration requestConfiguration = new RequestConfiguration(contractConfiguration, MockUtils.anEnvironment(), anInvalidPartnerConfiguration());

        // when calling the refundOrder method, an exception is thrown
        assertThrows(InvalidDataException.class, () -> sharegroopHttpClient.post(requestConfiguration, MockUtils.anOrderId(), "CANCEL", null));
    }

    @Test
    void cancelOrder_nominal() throws IOException {
        // given: Valid parameter  to create a request configuration
        RequestConfiguration requestConfiguration = new RequestConfiguration(MockUtils.aContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

        String content = "{\"success\":true,\"data\":{\"currency\":\"EUR\",\"ux\":\"collect\",\"ecard\":false,\"lastName\":\"Doe\",\"platformId\":\"pl_5ee79772-d68b-4e83-b334-b9b5c0349738\",\"delay\":8640,\"dueDate\":1574065897896,\"locale\":\"en\",\"status\":\"confirmed\",\"tenantId\":\"tn_359e7b91-71f7-4e24-bbb4-af750e959b3f\",\"trackId\":\"TRACK-1\",\"createdAt\":1573547497896,\"email\":\"martin@email.com\",\"integration\":\"front\",\"items\":[{\"name\":\"Product A\",\"description\":\"Description A\",\"amount\":12000,\"id\":\"itm_0d3d5178-f84f-4303-96e8-daf134efb27f\",\"quantity\":1,\"trackId\":\"TRACK-A\"}],\"firstName\":\"John\",\"tenantPlatform\":\"tn_359e7b91-71f7-4e24-bbb4-af750e959b3f#pl_5ee79772-d68b-4e83-b334-b9b5c0349738\",\"amountConfirmed\":4000,\"updatedAt\":1573547502945,\"nbShares\":3,\"amount\":12000,\"id\":\"ord_b1a50fa1-bf9c-4f3e-aa5c-0e3b24aaa79d\",\"secure3D\":true,\"type\":\"direct\"}}";
        StringResponse response = HttpTestUtils.mockStringResponse(200, "OK", content, null);

        doReturn(response).when(sharegroopHttpClient).execute(any(HttpRequestBase.class));

        // when : calling refund method
        SharegroopAPICallResponse result = sharegroopHttpClient.cancelOrder(requestConfiguration, MockUtils.anOrderId());

        // then
        assertNotNull(result);

        verify(http, never()).execute(any(HttpRequestBase.class));
    }

    static PartnerConfiguration anInvalidPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "://api.sandbox.sharegroop.com");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

}
