package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.service.LogoPaymentFormConfigurationService;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;

public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {
    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {

        /* TODO: voir si on a besoin d'implémenter cette méthode...
        Charge-t-on le JS ici ou dans le PaymentService ? (sachant qu'on a besoin de données de paiement)
         */

        return null;
    }
}