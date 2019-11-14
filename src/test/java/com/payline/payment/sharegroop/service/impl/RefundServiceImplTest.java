package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefundServiceImplTest {

    private static final String REFUND_RESPONSE_OK = "{" +
            "\"success\":true," +
            "\"data\":" +
            "{" +
            "\"currency\":\"EUR\"," +
            "\"lastName\":\"Doe\"," +
            "\"platformId\":\"pl_5ee79772-d68b-4e83-b334-b9b5c0349738\"," +
            "\"delay\":8640," +
            "\"dueDate\":1573737727739," +
            "\"status\":\"refunded\"," +
            "\"email\":\"martin@email.com\"," +
            "\"firstName\":\"John\"," +
            "\"id\":\"ord_7d4ca1a9-1c4e-47bd-9d1a-9330b605571d\"," +
            "\"toProcess\":1," +
            "\"ux\":\"collect\"," +
            "\"ecard\":false," +
            "\"locale\":\"en\"," +
            "\"trackId\":\"TRACK-1\"," +
            "\"createdAt\":1573219327739," +
            "\"integration\":\"front\"," +
            "\"items\":[" +
            "{" +
            "\"name\":\"Product A\"," +
            "\"description\":\"Description A\"," +
            "\"amount\":12000," +
            "\"id\":\"itm_9de81228-7034-4f17-a07a-c85b8da98cea\"," +
            "\"quantity\":1," +
            "\"trackId\":\"TRACK-A\"" +
            "}" +
            "]," +
            "\"amountConfirmed\":12000," +
            "\"updatedAt\":1573219550511," +
            "\"nbShares\":1," +
            "\"amount\":12000," +
            "\"secure3D\":true," +
            "\"type\":\"direct\"" +
            "}" +
            "}";



    @InjectMocks
    private RefundServiceImpl service;

    @Mock
    private SharegroopHttpClient httpClient;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void refund_RequestTestOK(){

    }



}
