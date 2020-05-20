package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

class PaymentServiceImplTest {

    @InjectMocks
    PaymentService service = new PaymentServiceImpl();

    @Mock
    SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequestStep1() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFormUpdated.class, response.getClass());
    }

    @Test
    void paymentRequestStep2() {
        SharegroopAPICallResponse apiResponse = SharegroopAPICallResponse.fromJson(MockUtils.aShareGroopResponse("confirmed"));
        Mockito.doReturn(apiResponse).when(sharegroopHttpClient).verifyOrder(Mockito.any(), Mockito.any());

        // init data
        Map<String, String> requestContextData = new HashMap<>();
        requestContextData.put("STEP", "STEP2");

        Map<String, String> formContextData = new HashMap<>();
        String jsCallback = "{\n" +
                "  \"order\":\"123123\",\n" +
                "  \"amount\": 100,\n" +
                "  \"auth\": \"foo123123\",\n" +
                "  \"email\":\"foo@bar.baz\",\n" +
                "  \"firstName\": \"foo\",\n" +
                "  \"lastName\": \"bar\",\n" +
                "  \"status\": \"authorized\"\n" +
                "}";
        formContextData.put("data", jsCallback);

        RequestContext context = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestContextData)
                .build();

        PaymentFormContext paymentFormContext = PaymentFormContext.PaymentFormContextBuilder
                .aPaymentFormContext()
                .withPaymentFormParameter(formContextData)
                .build();

        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withRequestContext(context)
                .withPaymentFormContext(paymentFormContext)
                .build();


        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
    }


    @Test
    void paymentRequestStep2WithoutData() {
        // init data
        Map<String, String> requestContextData = new HashMap<>();
        requestContextData.put("STEP", "STEP2");

        RequestContext context = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestContextData)
                .build();

        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withRequestContext(context)
                .build();

        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void paymentRequestStep3WithoutData() {
        // create mock
        SharegroopAPICallResponse apiResponse = SharegroopAPICallResponse.fromJson(MockUtils.aShareGroopResponse("confirmed"));
        Mockito.doReturn(apiResponse).when(sharegroopHttpClient).verifyOrder(Mockito.any(), Mockito.any());

        Map<String, String> requestContextData = new HashMap<>();
        requestContextData.put("STEP", "STEP3");

        RequestContext context = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestContextData)
                .build();

        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withRequestContext(context)
                .build();

        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void paymentRequestStepXXX() {
        // init data
        Map<String, String> requestContextData = new HashMap<>();
        requestContextData.put("STEP", "STEPXXX");

        RequestContext context = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestContextData)
                .build();

        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withRequestContext(context)
                .build();

        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }
}