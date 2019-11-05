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
     * Generate a valid {@link ContractConfiguration}.
     */
    // TODO: aContractconfiguration() suffit. surcharge si besoin de valeurs différentes.
    public static ContractConfiguration aContractConfigurationWithCollect() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();

        contractProperties.put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty("PrivateKey"));
        contractProperties.put(Constants.ContractConfigurationKeys.PUBLIC_KEY, new ContractProperty("PublicKey"));

        contractProperties.put(Constants.ContractConfigurationKeys.SECURE_3D, new ContractProperty("true"));
        contractProperties.put(Constants.ContractConfigurationKeys.UX, new ContractProperty("collect"));

        return new ContractConfiguration("Sharegroop", contractProperties);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    // TODO: aContractconfiguration() suffit. surcharge si besoin de valeurs différentes.
    public static ContractConfiguration aContractConfigurationWithPicking() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();

        contractProperties.put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty("PrivateKey"));
        contractProperties.put(Constants.ContractConfigurationKeys.PUBLIC_KEY, new ContractProperty("PublicKey"));

        contractProperties.put(Constants.ContractConfigurationKeys.SECURE_3D, new ContractProperty("true"));
        contractProperties.put(Constants.ContractConfigurationKeys.UX, new ContractProperty("picking"));

        return new ContractConfiguration("Sharegroop", contractProperties);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL_SANDBOX, "https://api.sandbox.sharegroop.com");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "https://api.sharegroop.com");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a invalid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aInvalidPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL_SANDBOX, "://api.sandbox.sharegroop.com");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "://api.sharegroop.com");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    public static StringResponse verifyPrivateKeyValidResponse() {
        return HttpTestUtils.mockStringResponse(400, "Bad Request", "{\"status\":400,\"success\":false,\"errors\":[\"should be object\"]}", null);
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

    public static StringResponse createOrderValidResponse() {
        String content = "{\"success\":true," +
                "\"data\":{" +
                "\"id\":\"ord_92aa7cfc-45df-4cf8-96f9-dd19b4bb3d09\"," +
                "\"platformId\":\"pl_5ee79772-d68b-4e83-b334-b9b5c0349738\"," +
                "\"amount\":10000," +
                "\"amountConfirmed\":0," +
                "\"delay\":8640," +
                "\"secure3D\":true," +
                "\"currency\":\"EUR\"," +
                "\"locale\":\"en\"," +
                "\"ux\":\"collect\"," +
                "\"type\":\"direct\"," +
                "\"status\":\"initiated\"," +
                "\"createdAt\":1572886069520," +
                "\"email\":\"captain@example.com\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Carter\"," +
                "\"trackId\":\"MY-INTERN-ID\"," +
                "\"items\":[{" +
                "\"id\":\"itm_e030779a-e943-46c4-95d8-0436bd678cd8\"," +
                "\"name\":\"Product A\"," +
                "\"amount\":2200," +
                "\"quantity\":1," +
                "\"trackId\":\"MY-ITEM-ID\"," +
                "\"description\":\"Description A\"}," +
                "{\"id\":\"itm_803cadd2-2bdd-4664-a926-571bd5c9314e\"," +
                "\"name\":\"Product B\"," +
                "\"amount\":5000," +
                "\"quantity\":1," +
                "\"description\":\"Description B\"}," +
                "{\"id\":\"itm_3eedca1c-622b-4cd3-b0b5-64d45a22d3a4\"," +
                "\"name\":\"Product C\"," +
                "\"amount\":2800," +
                "\"quantity\":1}]," +
                "\"dueDate\":1573404469576}}";

        return HttpTestUtils.mockStringResponse(200, "OK", content, null);
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
