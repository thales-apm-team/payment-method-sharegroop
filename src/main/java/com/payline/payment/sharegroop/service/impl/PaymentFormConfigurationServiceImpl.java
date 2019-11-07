package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.service.LogoPaymentFormConfigurationService;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;

public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {
    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {

        /* TODO: c'est probablement ici qu'on va construire le JS
        (insérer les bonnes valeurs dedans, à partir du contenu de PaymentFormConfigurationRequest).
        Et on va le renvoyer à Payline en lieu de place du formulaire de paiement
        -> voir l'exemple de Google-Pay

        Si on peut initialiser l'ordre directement dans le JS de paiement (sans avoir à faire un appel HTTP préalable),
        c'est peut-être le mieux. Ca évite de générer un ordre pour rien si, au final, l'acheteur ne va pas au bout du
        process de paiement.
        -> Valider la faisabilité avec le script JS en local !
        (Auquel cas, la méthode createOrder dans le client HTTP ne sert à rien... :/)
         */

        return null;
    }
}