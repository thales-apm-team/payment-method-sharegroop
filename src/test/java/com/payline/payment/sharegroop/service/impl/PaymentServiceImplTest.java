package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
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

        // create mock
        String jsonResponse = "{\n" +
                "  \"success\": true,\n" +
                "  \"data\": {\n" +
                "    \"id\": \"ord_64cfb50d-0053-4e23-975a-572f7d7dba98\",\n" +
                "    \"platformId\": \"pl_e9962604-78a9-41d6-9b26-0735514e8b16\",\n" +
                "    \"amount\": 14400,\n" +
                "    \"amountConfirmed\": 0,\n" +
                "    \"delay\": 8640,\n" +
                "    \"secure3D\": true,\n" +
                "    \"currency\": \"EUR\",\n" +
                "    \"locale\": \"en\",\n" +
                "    \"ux\": \"collect\",\n" +
                "    \"type\": \"direct\",\n" +
                "    \"status\": \"authorized\",\n" +
                "    \"createdAt\": 1553867170278,\n" +
                "    \"email\": \"captain@example.com\",\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"lastName\": \"Carter\",\n" +
                "    \"trackId\": \"MY-INTERN-ID\",\n" +
                "    \"dueDate\": 1554385570278,\n" +
                "    \"items\": [\n" +
                "      {\n" +
                "        \"id\": \"itm_8037cc50-e800-473a-b807-c80fe32d0606\",\n" +
                "        \"name\": \"Product B\",\n" +
                "        \"amount\": 5000,\n" +
                "        \"quantity\": 1,\n" +
                "        \"description\": \"Description B\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        SharegroopAPICallResponse apiResponse = SharegroopAPICallResponse.fromJson(jsonResponse);
        Mockito.doReturn(apiResponse).when(sharegroopHttpClient).verifyOrder(Mockito.any(), Mockito.any());

        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFormUpdated.class, response.getClass());
    }

    @Test
    void paymentRequestStep3() {
        Map<String, String> requestContextData = new HashMap<>();
        requestContextData.put("STEP", "STEP3");
        requestContextData.put("EMAIL", "foo@bar.baz");
        requestContextData.put("ORDER", "123123");
        requestContextData.put("STATUS", "authorized");


        RequestContext context = RequestContext.RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestContextData)
                .build();

        PaymentRequest request = MockUtils.aPaylinePaymentRequestBuilder()
                .withRequestContext(context)
                .build();

        PaymentResponse response = service.paymentRequest(request);
        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
    }
}