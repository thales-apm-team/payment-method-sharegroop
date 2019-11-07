package com.payline.payment.sharegroop.service.impl;

import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {

        /* TODO: ici, on va avoir besoin de récupérer les infos qui ont été renvoyées par l'API Sharegroop au JS
        lors du processus de paiement.
        Il doit y avoir une callback Javascript du widget Payline à appeler à ce moment-là, dans le JS, pour sauvegarder les données de retour.
        -> Creuser du côté de la constante JS_PARAM_VALUE_CALLBACK sur GooglePay...
        -> Voir dans le service PaymentServiceImpl de GooglePay de quelle façon on récupère ces données
        (de ce que je comprends, c'est dans le PaymentFormContext, avec la clé "data". Mais confirmer avec Jan.
         */

        return null;
    }
}
