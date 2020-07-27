package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.bean.notification.SharegroopNotificationResponse;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.PluginUtils;
import com.payline.pmapi.bean.common.Message;
import com.payline.pmapi.bean.common.TransactionCorrelationId;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.NotificationService;
import org.apache.logging.log4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LogManager.getLogger(NotificationServiceImpl.class);
    private static final String HASH_ALGORITHM = "HmacSHA256";
    private static final String WEBHOOK_SECRET_KEY = "whsk_f19f3281-eb6f-4d5f-a378-8df1d32f6b91";
    private static final String SG_SIGNATURE = "SG-Signature";
    private static final int HTTP_OK = 200;


    @Override
    public NotificationResponse parse(NotificationRequest request) {
        NotificationResponse notificationResponse = new IgnoreNotificationResponse();

        try {

            String signature = request.getHeaderInfos().get(SG_SIGNATURE);

            // init data
            String content = PluginUtils.inputStreamToString(request.getContent());
            SharegroopNotificationResponse sharegroopNotificationResponse = SharegroopNotificationResponse.fromJson(content);

            if (content != null && sharegroopNotificationResponse != null) {
                // Check notification's signature
                if (verifySignature(WEBHOOK_SECRET_KEY, content, signature.replace("v1=", ""))) {
                    // Check if it is a COMPLETED event
                    if (Constants.SharegroopEventKeys.COMPLETED.equals(sharegroopNotificationResponse.getEvent())) {
                        PaymentResponse paymentResponse = PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                                .withStatusCode(Integer.toString(HTTP_OK))
                                .withTransactionDetails(new EmptyTransactionDetails())
                                .withPartnerTransactionId(sharegroopNotificationResponse.getId())
                                .withMessage(new Message(Message.MessageType.SUCCESS, sharegroopNotificationResponse.getEvent()))
                                .build();
                        notificationResponse = buildResponse(paymentResponse, sharegroopNotificationResponse.getId());
                    }
                } else {
                    LOGGER.error("Notification signature is not verified");
                }
            } else {
                LOGGER.error("Notification content incorrect -  content : {} - sharegroopNotificationResponse : {}", content, sharegroopNotificationResponse);
            }
        }catch (RuntimeException e){
            LOGGER.error("Error while processing notification", e);
        }

        return notificationResponse;
    }

    /**
     * verify if the signature is valid
     *
     * @param webhookSecretKey the Key to use to verify the signature
     * @param content          the content to compare
     * @param signature        the signature of the message
     * @return
     */
    public boolean verifySignature(String webhookSecretKey, String content, String signature) {
        Boolean status = false;

        if (webhookSecretKey != null && content != null && signature != null) {
            String hash = hashMac(content, webhookSecretKey);
            if (signature.equals(hash)) {
                status = true;
            }
        }else{
            LOGGER.error("Incorrect data - webhookSecretKey : {} - content : {} - signature : {}", webhookSecretKey,content,signature);
        }

        return status;
    }

    /**
     * Encryption of the text with a secret key
     *
     * @param text
     * @param secretKey
     * @return
     * @throws SignatureException
     */
    public static String hashMac(String text, String secretKey) {
        if(secretKey == null||secretKey.isEmpty()||text == null){
            return "";
        }

        try {
            Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            final byte[] hmac = mac.doFinal(text.getBytes());
            return toHexString(hmac);
        } catch (NoSuchAlgorithmException e) {
            throw new PluginException("error building signature, no such algorithm " + HASH_ALGORITHM);
        } catch (InvalidKeyException e) {
            throw new PluginException("error building signature, invalid key " + HASH_ALGORITHM);
        }
    }


    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

       try(Formatter formatter = new Formatter(sb)) {
           for (byte b : bytes) {
               formatter.format("%02x", b);
           }
       }

        return sb.toString();
    }


    @Override
    public void notifyTransactionStatus(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        // does nothing
    }


    private PaymentResponseByNotificationResponse buildResponse(PaymentResponse paymentResponse, String partnerTransactionId ){
        return PaymentResponseByNotificationResponse.PaymentResponseByNotificationResponseBuilder.aPaymentResponseByNotificationResponseBuilder()
                .withPaymentResponse( paymentResponse )
                .withTransactionCorrelationId(
                        TransactionCorrelationId.TransactionCorrelationIdBuilder
                                .aCorrelationIdBuilder()
                                .withType( TransactionCorrelationId.CorrelationIdType.PARTNER_TRANSACTION_ID )
                                .withValue( partnerTransactionId )
                                .build()
                )
                .withHttpStatus(204)
                .build();
    }
}

