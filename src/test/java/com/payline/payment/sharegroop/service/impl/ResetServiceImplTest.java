package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.service.JsonService;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class ResetServiceImplTest {

    private static final String RESET_RESPONSE_KO = "{\n" +
            "    \"status\": 400,\n" +
            "    \"success\": false,\n" +
            "    \"errors\": [\n" +
            "        \"should be object\"\n" +
            "    ]\n" +
            "}";

    private static final String RESET_UNAUTHORIZED = "{\n" +
            "    \"message\": \"Unauthorized\"\n" +
            "}";


    @InjectMocks
    private ResetServiceImpl service;

    @Mock
    private SharegroopHttpClient httpClient;

    private final JsonService jsonService = JsonService.getInstance();

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void reset_RequestTestOK() {
        SharegroopAPICallResponse sharegroopResponse = jsonService.fromJson(MockUtils.aShareGroopResponse("refunded"), SharegroopAPICallResponse.class);
        Mockito.doReturn(sharegroopResponse).when(httpClient).cancelOrder(any(), anyString());
        Mockito.doReturn(sharegroopResponse).when(httpClient).verifyOrder(any(), anyString());

        ResetRequest request = MockUtils.aPaylineResetRequest();
        ResetResponse response = service.resetRequest(request);

        Assertions.assertEquals(ResetResponseSuccess.class, response.getClass());
    }

    @Test
    void reset_RequestTestKO() {
        SharegroopAPICallResponse sharegroopResponse = jsonService.fromJson(RESET_RESPONSE_KO, SharegroopAPICallResponse.class);
        Mockito.doReturn(sharegroopResponse).when(httpClient).cancelOrder(any(), anyString());

        ResetRequest request = MockUtils.aPaylineResetRequest();
        ResetResponse response = service.resetRequest(request);

        Assertions.assertEquals(ResetResponseFailure.class, response.getClass());
        ResetResponseFailure responseFailure = (ResetResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getErrorCode());
        Assertions.assertNotNull(responseFailure.getFailureCause());
    }

    @Test
    void reset_RequestTestKO2() {
        SharegroopAPICallResponse sharegroopResponse = jsonService.fromJson(RESET_UNAUTHORIZED, SharegroopAPICallResponse.class);
        Mockito.doReturn(sharegroopResponse).when(httpClient).cancelOrder(any(), anyString());

        ResetRequest request = MockUtils.aPaylineResetRequest();
        ResetResponse response = service.resetRequest(request);

        Assertions.assertEquals(ResetResponseFailure.class, response.getClass());
        ResetResponseFailure responseFailure = (ResetResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getErrorCode());
        Assertions.assertNotNull(responseFailure.getFailureCause());
    }


    @Test
    void reset_RequestTestKO3() {
        SharegroopAPICallResponse sharegroopResponse = jsonService.fromJson("", SharegroopAPICallResponse.class);
        Mockito.doReturn(sharegroopResponse).when(httpClient).cancelOrder(any(), anyString());

        ResetRequest request = MockUtils.aPaylineResetRequest();
        ResetResponse response = service.resetRequest(request);

        Assertions.assertEquals(ResetResponseFailure.class, response.getClass());
        ResetResponseFailure responseFailure = (ResetResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getErrorCode());
        Assertions.assertNotNull(responseFailure.getFailureCause());
    }

    @Test
    void reset_RequestTestException() {
        PluginException exception = new PluginException("foo");
        Mockito.doThrow(exception).when(httpClient).cancelOrder(any(), anyString());

        ResetRequest request = MockUtils.aPaylineResetRequest();
        ResetResponse response = service.resetRequest(request);

        Assertions.assertEquals(ResetResponseFailure.class, response.getClass());
        ResetResponseFailure responseFailure = (ResetResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getErrorCode());
        Assertions.assertNotNull(responseFailure.getFailureCause());
    }

    @Test
    void can(){
        Assertions.assertFalse(service.canPartial());
        Assertions.assertFalse(service.canMultiple());
    }

}
