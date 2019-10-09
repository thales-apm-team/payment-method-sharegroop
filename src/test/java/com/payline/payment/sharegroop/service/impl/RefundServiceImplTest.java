package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class RefundServiceImplTest {
    private static final String REFUND_RESPONSE_KO = "{\n" +
            "    \"status\": 400,\n" +
            "    \"success\": false,\n" +
            "    \"errors\": [\n" +
            "        \"should be object\"\n" +
            "    ]\n" +
            "}";

    private static final String REFUND_UNAUTHORIZED = "{\n" +
            "    \"message\": \"Unauthorized\"\n" +
            "}";


    @InjectMocks
    private RefundServiceImpl service;

    @Mock
    private SharegroopHttpClient httpClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void refund_RequestTestOK() {
        SharegroopAPICallResponse sharegroopResponse = SharegroopAPICallResponse.fromJson(MockUtils.aShareGroopResponse("refunded"));
        Mockito.doReturn(sharegroopResponse).when(httpClient).refundOrder(any(), anyString());

        RefundRequest request = MockUtils.aPaylineRefundRequest();
        RefundResponse response = service.refundRequest(request);

        Assertions.assertEquals(RefundResponseSuccess.class, response.getClass());
    }

    @Test
    void refund_RequestTestKO() {
        SharegroopAPICallResponse sharegroopResponse = SharegroopAPICallResponse.fromJson(REFUND_RESPONSE_KO);
        Mockito.doReturn(sharegroopResponse).when(httpClient).refundOrder(any(), anyString());

        RefundRequest request = MockUtils.aPaylineRefundRequest();
        RefundResponse response = service.refundRequest(request);

        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getErrorCode());
        Assertions.assertNotNull(responseFailure.getFailureCause());
    }

    @Test
    void refund_RequestTestKO2() {
        SharegroopAPICallResponse sharegroopResponse = SharegroopAPICallResponse.fromJson(REFUND_UNAUTHORIZED);
        Mockito.doReturn(sharegroopResponse).when(httpClient).refundOrder(any(), anyString());

        RefundRequest request = MockUtils.aPaylineRefundRequest();
        RefundResponse response = service.refundRequest(request);

        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getErrorCode());
        Assertions.assertNotNull(responseFailure.getFailureCause());
    }


    @Test
    void refund_RequestTestKO3() {
        SharegroopAPICallResponse sharegroopResponse = SharegroopAPICallResponse.fromJson("");
        Mockito.doReturn(sharegroopResponse).when(httpClient).refundOrder(any(), anyString());

        RefundRequest request = MockUtils.aPaylineRefundRequest();
        RefundResponse response = service.refundRequest(request);

        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getErrorCode());
        Assertions.assertNotNull(responseFailure.getFailureCause());
    }

    @Test
    void refund_RequestTestException() {
        PluginException exception = new PluginException("foo");
        Mockito.doThrow(exception).when(httpClient).refundOrder(any(), anyString());

        RefundRequest request = MockUtils.aPaylineRefundRequest();
        RefundResponse response = service.refundRequest(request);

        Assertions.assertEquals(RefundResponseFailure.class, response.getClass());
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assertions.assertNotNull(responseFailure.getErrorCode());
        Assertions.assertNotNull(responseFailure.getFailureCause());
    }

    @Test
    void can() {
        Assertions.assertFalse(service.canPartial());
        Assertions.assertFalse(service.canMultiple());
    }
}
