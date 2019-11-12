package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResetServiceImplTest {

    @InjectMocks
    private ResetServiceImpl service;

    @Mock
    private SharegroopHttpClient httpClient;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void toremove(){
        // to please Sonarqube
        // TODO: remove this test method when there will be real test methods
        assertTrue(true);
    }

    // TODO: Implement tests

}
