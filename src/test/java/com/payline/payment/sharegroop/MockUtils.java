package com.payline.payment.sharegroop;

import com.payline.payment.sharegroop.utils.Constants;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;

import java.util.HashMap;
import java.util.Map;

public class MockUtils {
    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment(){
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfigurationWithCollect(){
        Map<String, ContractProperty> contractProperties = new HashMap<>();

        contractProperties.put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty( System.getProperty("project.clientPrivateKey")));

        // TODO : Ajouter la clé public dans les variables système
        contractProperties.put(Constants.ContractConfigurationKeys.PUBLIC_KEY, new ContractProperty( System.getProperty("project.clientPublicKey")));

        contractProperties.put(Constants.ContractConfigurationKeys.SECURE_3D, new ContractProperty("true"));
        contractProperties.put(Constants.ContractConfigurationKeys.UX, new ContractProperty("collect"));

        return new ContractConfiguration("Sharegroop", contractProperties);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfigurationWithPicking(){
        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty( System.getProperty("project.clientPrivateKey") ));
        // TODO : Ajouter la clé public dans les variables système
        contractProperties.put(Constants.ContractConfigurationKeys.PUBLIC_KEY, new ContractProperty( System.getProperty("project.clientPublicKey")));

        contractProperties.put(Constants.ContractConfigurationKeys.SECURE_3D, new ContractProperty("true"));
        contractProperties.put(Constants.ContractConfigurationKeys.UX, new ContractProperty("picking"));

        return new ContractConfiguration("Sharegroop", contractProperties);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration(){
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL_SANDBOX, "https://api.sandbox.sharegroop.com");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "https://api.sharegroop.com");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration( partnerConfigurationMap, sensitiveConfigurationMap );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a invalid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aInvalidPartnerConfiguration(){
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL_SANDBOX, "://api.sandbox.sharegroop.com");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "://api.sharegroop.com");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration( partnerConfigurationMap, sensitiveConfigurationMap );
    }
    /**------------------------------------------------------------------------------------------------------------------*/

}
