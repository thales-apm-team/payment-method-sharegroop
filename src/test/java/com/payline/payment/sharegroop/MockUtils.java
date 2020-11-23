package com.payline.payment.sharegroop;

import com.payline.payment.sharegroop.bean.payment.Order;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;

import java.math.BigInteger;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class MockUtils {
    private static final String TRANSACTIONID = "123456789012345678901";
    private static final String PARTNER_TRANSACTIONID = "098765432109876543210";


    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }

    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "https://api.sandbox.sharegroop.com");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_WIDGET_URL, "https://widget.sandbox.sharegroop.com/widget.js");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();
        sensitiveConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_WEBHOOK_SECRET_KEY, "thisIsAfakeWebHookKey");

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest aPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withLocale(Locale.getDefault())
                .build();
    }


    public static String anOrderId() {
        return "ord_92aa7cfc-45df-4cf8-96f9-dd19b4bb3d09";
    }


    public static Order anOrder() {
        String body = "{\n" +
                "\t\"amount\": 10000,\n" +
                "\t\"ux\": \"collect\",\n" +
                "\t\"locale\": \"en\",\n" +
                "\t\"currency\": \"EUR\",\n" +
                "\t\"secure3D\": true,\n" +
                "\t\"email\": \"captain@example.com\",\n" +
                "\t\"firstName\": \"John\",\n" +
                "\t\"lastName\": \"Carter\",\n" +
                "\t\"trackId\": \"MY-INTERN-ID\",\n" +
                "\t\"notifyUrl\": \"https://my.domain/my-endpoint\",\n" +
                "\t\"items\": [\n" +
                "        {\n" +
                "          \"trackId\": \"MY-ITEM-ID\",\n" +
                "          \"name\": \"Product A\",\n" +
                "          \"description\": \"Description A\",\n" +
                "          \"amount\": 2200,\n" +
                "          \"quantity\": 1\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Product B\",\n" +
                "          \"description\": \"Description B\",\n" +
                "          \"amount\": 5000\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Product C\",\n" +
                "          \"amount\": 2800\n" +
                "        }\n" +
                "      ]\n" +
                "}";

        return Order.fromJson(body);
    }

    /**
     * Generate a valid, but not complete, {@link com.payline.pmapi.bean.payment.Order}
     */
    public static com.payline.pmapi.bean.payment.Order aPaylineOrder() {
        List<com.payline.pmapi.bean.payment.Order.OrderItem> items = new ArrayList<>();

        items.add(com.payline.pmapi.bean.payment.Order.OrderItem.OrderItemBuilder
                .anOrderItem()
                .withReference("foo")
                .withAmount(aPaylineAmount())
                .withQuantity((long) 1)
                .build());

        return com.payline.pmapi.bean.payment.Order.OrderBuilder.anOrder()
                .withDate(new Date())
                .withAmount(aPaylineAmount())
                .withItems(items)
                .withReference("ORDER-REF-123456")
                .build();
    }

    /**
     * Generate a valid Payline Amount.
     */
    public static com.payline.pmapi.bean.common.Amount aPaylineAmount() {
        return new com.payline.pmapi.bean.common.Amount(BigInteger.valueOf(1000), Currency.getInstance("EUR"));
    }

    /**
     * @return a valid user agent.
     */
    public static String aUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0";
    }

    /**
     * Generate a valid {@link Browser}.
     */
    public static Browser aBrowser() {
        return Browser.BrowserBuilder.aBrowser()
                .withLocale(Locale.getDefault())
                .withIp("192.168.0.1")
                .withUserAgent(aUserAgent())
                .build();
    }

    /**
     * Generate a valid {@link Buyer}.
     */
    public static Buyer aBuyer() {
        return Buyer.BuyerBuilder.aBuyer()
                .withFullName(new Buyer.FullName("Marie", "Durand", "1"))
                .withEmail("foo@bar.baz")
                .build();
    }

    /**
     * Generate a valid {@link PaymentFormContext}.
     */
    public static PaymentFormContext aPaymentFormContext() {
        Map<String, String> paymentFormParameter = new HashMap<>();

        return PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withPaymentFormParameter(paymentFormParameter)
                .withSensitivePaymentFormParameter(new HashMap<>())
                .build();
    }

    /**
     * Generate a valid {@link ContractParametersCheckRequest}.
     */
    public static ContractParametersCheckRequest aContractParametersCheckRequest() {
        return aContractParametersCheckRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link ContractParametersCheckRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static ContractParametersCheckRequest.CheckRequestBuilder aContractParametersCheckRequestBuilder() {
        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo(anAccountInfo())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    /**
     * Generate a valid {@link PaymentFormConfigurationRequest}.
     */
    public static PaymentFormConfigurationRequest aPaymentFormConfigurationRequest() {
        return aPaymentFormConfigurationRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentFormConfigurationRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder aPaymentFormConfigurationRequestBuilder() {
        return PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withAmount(aPaylineAmount())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.FRANCE)
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    /**
     * Generate a valid {@link PaymentRequest}.
     */
    public static PaymentRequest aPaylinePaymentRequest() {
        return aPaylinePaymentRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentRequest.Builder aPaylinePaymentRequestBuilder() {
        return PaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withCaptureNow(true)
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.FRANCE)
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPaymentFormContext(aPaymentFormContext())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId(TRANSACTIONID);
    }

    public static RefundRequest aPaylineRefundRequest() {
        return aPaylineRefundRequestBuilder().build();
    }

    public static RefundRequest.RefundRequestBuilder aPaylineRefundRequestBuilder() {
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(aPaylineAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTIONID)
                .withPartnerTransactionId(PARTNER_TRANSACTIONID)
                .withPartnerConfiguration(aPartnerConfiguration());
    }


    public static ResetRequest aPaylineResetRequest() {
        return aPaylineResetRequestBuilder().build();


    }

    public static ResetRequest.ResetRequestBuilder aPaylineResetRequestBuilder() {
        return ResetRequest.ResetRequestBuilder.aResetRequest()
                .withAmount(aPaylineAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTIONID)
                .withPartnerTransactionId(PARTNER_TRANSACTIONID)
                .withPartnerConfiguration(aPartnerConfiguration());
    }


    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance.
     */
    public static Map<String, String> anAccountInfo() {
        return anAccountInfo(aContractConfiguration());
    }
    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance,
     * from the given {@link ContractConfiguration}.
     *
     * @param contractConfiguration The model object from which the properties will be copied
     */
    public static Map<String, String> anAccountInfo(ContractConfiguration contractConfiguration) {
        Map<String, String> accountInfo = new HashMap<>();
        for (Map.Entry<String, ContractProperty> entry : contractConfiguration.getContractProperties().entrySet()) {
            accountInfo.put(entry.getKey(), entry.getValue().getValue());
        }
        return accountInfo;
    }
    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfiguration() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty("PrivateKey"));
        contractProperties.put(Constants.ContractConfigurationKeys.PUBLIC_KEY, new ContractProperty("PublicKey"));
        contractProperties.put(Constants.ContractConfigurationKeys.SECURE_3D, new ContractProperty("true"));
        contractProperties.put(Constants.ContractConfigurationKeys.UX, new ContractProperty("collect"));

        return new ContractConfiguration("Sharegroop", contractProperties);
    }

    /**
     * Generate a json shareGroop response message
     * @param status
     * @return
     */
    public static String aShareGroopResponse(String status) {
        return "{" +
                "\"success\":true," +
                "\"data\":" +
                "{" +
                "\"currency\":\"EUR\"," +
                "\"lastName\":\"Doe\"," +
                "\"platformId\":\"pl_5ee79772-d68b-4e83-b334-b9b5c0349738\"," +
                "\"delay\":8640," +
                "\"dueDate\":1573737727739," +
                "\"status\":\"" + status + "\"," +
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
    }

}
