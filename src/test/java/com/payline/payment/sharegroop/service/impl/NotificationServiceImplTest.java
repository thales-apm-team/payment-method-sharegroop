package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class NotificationServiceImplTest {

    @InjectMocks
    NotificationServiceImpl service = new NotificationServiceImpl();

    @Test
    void paymentRequestStep1() {

        Boolean flag = false;
        String content = "{\"event\":\"order.refunded\",\"id\":\"ord_326cdac6-05d9-4dc1-bd35-7ea70d997721\",\"date\":1595321904259}";
        String signature = "a3f2897e6341f4ba9b722682c4eb3e684bef86107ffd0940d15d15eb59dcbec4";
        flag = service.verifySignature(MockUtils.getWebhookSecretKey(), content, signature);

        Assertions.assertEquals(true, flag);
    }

    @Test
    void parseCompleted() {

        String content = "{\"event\":\"order.completed\",\"id\":\"ord_326cdac6-05d9-4dc1-bd35-7ea70d997721\",\"date\":1595321904259}";
        String signature = "v1=6fc8231da2a7142e9acb1d4dbacdb7c4fea7b61c6abdaa5a0cdb6641e42bb23c";

        HashMap<String, String> headerInfo = new HashMap<>();
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
        Assertions.assertEquals(PaymentResponseSuccess.class, ((PaymentResponseByNotificationResponse) notificationResponse).getPaymentResponse().getClass());
        Assertions.assertEquals("ord_326cdac6-05d9-4dc1-bd35-7ea70d997721", ((PaymentResponseSuccess) ((PaymentResponseByNotificationResponse) notificationResponse).getPaymentResponse()).getPartnerTransactionId());
    }

    @Test
    void parseRefunded() {

        String content = "{\"event\":\"order.refunded\",\"id\":\"ord_326cdac6-05d9-4dc1-bd35-7ea70d997721\",\"date\":1595321904259}";
        String signature = "v1=a3f2897e6341f4ba9b722682c4eb3e684bef86107ffd0940d15d15eb59dcbec4";

        HashMap<String, String> headerInfo = new HashMap<>();
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
