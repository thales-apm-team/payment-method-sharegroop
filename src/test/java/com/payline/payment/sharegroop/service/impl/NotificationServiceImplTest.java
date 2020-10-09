package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class NotificationServiceImplTest {

    @InjectMocks
    @Spy
    NotificationServiceImpl service = new NotificationServiceImpl();

    private String key = "this is a Key";
    private String content = "this is a content";
    private String signature = "a3f2897e6341f4ba9b722682c4eb3e684bef86107ffd0940d15d15eb59dcbec4";


    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void verifySignature() {
        Mockito.doReturn(signature).when(service).hashMac(anyString(), anyString());

        Assertions.assertTrue(service.verifySignature(key, content, signature));
    }

    @Test
    void verifySignatureContentNull() {
        Assertions.assertFalse(service.verifySignature(key, null, signature));
    }

    @Test
    void verifyHashMacOK() {
        String hash = service.hashMac(content, key);
        Assertions.assertEquals("062dbf8ef69280ee6c11d4bd6fb7f6093533eb46a59323353104eeac2c785ae7", hash);
    }

    @Test
    void verifyHashMacKeyNull() {
        PluginException exception = Assertions.assertThrows(PluginException.class, () -> service.hashMac(content, null));
        Assertions.assertEquals("error building signature, empty key", exception.getMessage());
    }

    @Test
    void verifyHashMacKeyEmpty() {
        PluginException exception = Assertions.assertThrows(PluginException.class, () -> service.hashMac(content, ""));
        Assertions.assertEquals("error building signature, empty key", exception.getMessage());
    }

    @Test
    void verifyHashMacContentNull() {
        PluginException exception = Assertions.assertThrows(PluginException.class, () -> service.hashMac(null, key));
        Assertions.assertEquals("error building signature, text is null", exception.getMessage());
    }

    @Test
    void parseCompleted() {
        String content = "{\"event\":\"order.completed\",\"id\":\"ord_326cdac6-05d9-4dc1-bd35-7ea70d997721\",\"date\":1595321904259}";
        String signature = "v1=6fc8231da2a7142e9acb1d4dbacdb7c4fea7b61c6abdaa5a0cdb6641e42bb23c";

        Map<String, String> headerInfo = new HashMap<>();
        headerInfo.put("Content-Type", "application/json");
        headerInfo.put("SG-Signature", signature);

        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        NotificationRequest request = NotificationRequest.NotificationRequestBuilder.aNotificationRequest()
                .withHeaderInfos(headerInfo)
                .withContent(stream)
                .withHttpMethod("POST")
                .withPathInfo("thisIsAPath")
                .withEnvironment(MockUtils.anEnvironment())
                .withPartnerConfiguration(MockUtils.aPartnerConfiguration())
                .build();


        Mockito.doReturn(true).when(service).verifySignature(any(), anyString(), anyString());

        NotificationResponse notificationResponse = service.parse(request);
        Assertions.assertNotNull(notificationResponse);
        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, notificationResponse.getClass());
        Assertions.assertEquals(PaymentResponseSuccess.class, ((PaymentResponseByNotificationResponse) notificationResponse).getPaymentResponse().getClass());
        Assertions.assertEquals("ord_326cdac6-05d9-4dc1-bd35-7ea70d997721", ((PaymentResponseSuccess) ((PaymentResponseByNotificationResponse) notificationResponse).getPaymentResponse()).getPartnerTransactionId());
    }

    @Test
    void parseRefunded() {

        String content = "{\"event\":\"order.refunded\",\"id\":\"ord_326cdac6-05d9-4dc1-bd35-7ea70d997721\",\"date\":1595321904259}";
        String signature = "v1=a3f2897e6341f4ba9b722682c4eb3e684bef86107ffd0940d15d15eb59dcbec4";

        Map<String, String> headerInfo = new HashMap<>();
        headerInfo.put("Content-Type", "application/json");
        headerInfo.put("SG-Signature", signature);


        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        NotificationRequest request = NotificationRequest.NotificationRequestBuilder.aNotificationRequest()

                .withHeaderInfos(headerInfo)
                .withContent(stream)
                .withHttpMethod("POST")
                .withPathInfo("thisIsAPath")
                .withEnvironment(MockUtils.anEnvironment())
                .build();

        NotificationResponse notificationResponse = service.parse(request);
        Assertions.assertNotNull(notificationResponse);
        Assertions.assertEquals(IgnoreNotificationResponse.class, notificationResponse.getClass());
    }

}
