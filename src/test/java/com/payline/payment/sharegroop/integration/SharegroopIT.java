package com.payline.payment.sharegroop.integration;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.service.impl.ConfigurationServiceImpl;
import com.payline.payment.sharegroop.service.impl.PaymentServiceImpl;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.junit.jupiter.api.Test;

/**
 * This is an integration test class to validate the full payment process, via the partner API.
 * It must be run with several system property set on the JVM */

public class SharegroopIT extends AbstractPaymentIntegration{
    ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl();
    PaymentServiceImpl paymentService = new PaymentServiceImpl();

    @Override
    protected ContractConfiguration generateContractConfiguration() {
        return MockUtils.aContractConfiguration();
    }

    @Override
    protected PartnerConfiguration generatePartnerConfiguration() {
        return MockUtils.aPartnerConfiguration();
    }

    @Override
    protected PaymentFormContext generatePaymentFormContext() {
        return MockUtils.aPaymentFormContext();
    }

    @Override
    protected String payOnPartnerWebsite(String url) {
        return null;
    }

    @Override
    protected String cancelOnPartnerWebsite(String url) {
        return null;
    }

    @Test
    protected void run() {
    }
}
