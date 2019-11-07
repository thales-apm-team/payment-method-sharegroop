package com.payline.payment.sharegroop;

import com.payline.payment.sharegroop.bean.payment.Order;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.http.HttpTestUtils;
import com.payline.payment.sharegroop.utils.http.StringResponse;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MockUtils {
    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "https://api.sandbox.sharegroop.com");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest aPaymentFormLogoRequest(){
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration( aContractConfiguration() )
                .withEnvironment( anEnvironment() )
                .withPartnerConfiguration( aPartnerConfiguration() )
                .withLocale( Locale.getDefault() )
                .build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /* TODO: à supprimer. Cette méthode ne sera a priori utilisée que dans une seule classe de test. C'est un cas spécifique.
    Donc inutile de créer une méthode de mock générique pour cela.
     */
    /**
     * Generate a invalid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aInvalidPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "://api.sandbox.sharegroop.com");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    // TODO: si cette méthode n'est utilisée qu'à un seul endroit, elle n'a pas sa place ici. La classe MockUtils n'e doit pas être un fourre-tout :)
    public static StringResponse verifyPrivateKeyValidResponse() {
        return HttpTestUtils.mockStringResponse(400, "Bad Request", "{\"status\":400,\"success\":false,\"errors\":[\"should be object\"]}", null);
    }
    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    public static String anOrderId() {
        return "ord_92aa7cfc-45df-4cf8-96f9-dd19b4bb3d09";
    }
    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
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

    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link ContractParametersCheckRequest}.
     */
    public static ContractParametersCheckRequest aContractParametersCheckRequest(){
        return aContractParametersCheckRequestBuilder().build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a builder for a valid {@link ContractParametersCheckRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static ContractParametersCheckRequest.CheckRequestBuilder aContractParametersCheckRequestBuilder(){
        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo( anAccountInfo() )
                .withContractConfiguration( aContractConfiguration() )
                .withEnvironment( anEnvironment() )
                .withLocale( Locale.getDefault() )
                .withPartnerConfiguration( aPartnerConfiguration() );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance.
     */
    public static Map<String, String> anAccountInfo(){
        return anAccountInfo( aContractConfiguration() );
    }
    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance,
     * from the given {@link ContractConfiguration}.
     *
     * @param contractConfiguration The model object from which the properties will be copied
     */
    public static Map<String, String> anAccountInfo( ContractConfiguration contractConfiguration ){
        Map<String, String> accountInfo = new HashMap<>();
        for( Map.Entry<String, ContractProperty> entry : contractConfiguration.getContractProperties().entrySet() ){
            accountInfo.put(entry.getKey(), entry.getValue().getValue());
        }
        return accountInfo;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfiguration(){
        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty( "PrivateKey" ));
        contractProperties.put(Constants.ContractConfigurationKeys.PUBLIC_KEY, new ContractProperty( "PublicKey" ));
        contractProperties.put(Constants.ContractConfigurationKeys.SECURE_3D, new ContractProperty( "true" ));
        contractProperties.put(Constants.ContractConfigurationKeys.UX, new ContractProperty( "collect" ));

        return new ContractConfiguration("Sharegroop", contractProperties);
    }
    /**------------------------------------------------------------------------------------------------------------------*/

}
