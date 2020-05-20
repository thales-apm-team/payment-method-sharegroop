package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PaymentFormConfigurationServiceImplTest {
    private final PaymentFormConfigurationServiceImpl service = new PaymentFormConfigurationServiceImpl();

    @Test
    void getPaymentFormConfiguration() {
        PaymentFormConfigurationRequest request = MockUtils.aPaymentFormConfigurationRequest();
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration(request);

        Assertions.assertNotNull(response);
    }
}