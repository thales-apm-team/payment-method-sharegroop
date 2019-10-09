package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.service.LogoPaymentFormConfigurationService;
import com.payline.payment.sharegroop.utils.i18n.I18nService;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;

import java.util.Locale;

public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {
    private static final String PAYMENT_BUTTON_TEXT = "payment.form.config.button.text";
    private static final String PAYMENT_BUTTON_DESC = "payment.form.config.description";

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        Locale locale = paymentFormConfigurationRequest.getLocale();

        NoFieldForm form = NoFieldForm.NoFieldFormBuilder.aNoFieldForm()
                .withButtonText(i18n.getMessage(PAYMENT_BUTTON_TEXT, locale))
                .withDescription(i18n.getMessage(PAYMENT_BUTTON_DESC, locale))
                .withDisplayButton(true)
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(form)
                .build();
    }
}