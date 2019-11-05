package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.payment.sharegroop.utils.i18n.I18nService;
import com.payline.payment.sharegroop.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.CheckboxParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.PasswordParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.service.ConfigurationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {

    private static final class UX {
        private static final String COLLECT = "collect";
        private static final String PICKING = "picking";
    }

    private I18nService i18n = I18nService.getInstance();
    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();
    private SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();

    /**------------------------------------------------------------------------------------------------------------------*/
    @Override
    public List<AbstractParameter> getParameters(Locale locale) {

        List<AbstractParameter> parameters = new ArrayList<>();

        // PUBLIC KEY
        InputParameter publicKey = new InputParameter();
        publicKey.setKey( Constants.ContractConfigurationKeys.PUBLIC_KEY );
        publicKey.setLabel( i18n.getMessage("contract.PUBLIC_KEY.label", locale) );
        publicKey.setDescription( i18n.getMessage("contract.PUBLIC_KEY.description", locale) );
        publicKey.setRequired( true );
        parameters.add( publicKey );

        // PRIVATE KEY
        PasswordParameter privateKey = new PasswordParameter();
        privateKey.setKey( Constants.ContractConfigurationKeys.PRIVATE_KEY );
        privateKey.setLabel( i18n.getMessage("contract.PRIVATE_KEY.label", locale) );
        privateKey.setDescription( i18n.getMessage("contract.PRIVATE_KEY.description", locale) );
        privateKey.setRequired( true );
        parameters.add( privateKey );


        // UX
        ListBoxParameter uX = new ListBoxParameter();
        uX.setKey( Constants.ContractConfigurationKeys.UX );
        uX.setLabel( i18n.getMessage("contract.UX.label", locale) );
        uX.setDescription( i18n.getMessage("contract.UX.description", locale) );
        Map<String, String> uXValues = new HashMap<>();
        uXValues.put(UX.COLLECT, UX.COLLECT);
        uXValues.put(UX.PICKING, UX.PICKING);
        uX.setList( uXValues );
        uX.setRequired( true );
        uX.setValue( UX.COLLECT );
        parameters.add( uX );

        // SECURE_3D
        CheckboxParameter secure3D = new CheckboxParameter();
        secure3D.setKey(Constants.ContractConfigurationKeys.SECURE_3D);
        secure3D.setLabel(i18n.getMessage("contract.SECURE_3D.label",locale));
        secure3D.setDescription(i18n.getMessage("contract.SECURE_3D.description",locale));
        secure3D.setRequired(true);
        parameters.add(secure3D);

        return parameters;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {

        final Map<String, String> errors = new HashMap<>();

        Map<String, String> accountInfo = contractParametersCheckRequest.getAccountInfo();

        Locale locale = contractParametersCheckRequest.getLocale();

        // check required fields
        for( AbstractParameter param : this.getParameters( locale ) ){
            if( param.isRequired() && accountInfo.get( param.getKey() ) == null ){
                String message = i18n.getMessage("contract." + param.getKey() + ".requiredError", locale);
                errors.put( param.getKey(), message );
            }
        }

        // If client private key is missing, no need to go further, as it is required
        String clientPrivateKey = Constants.ContractConfigurationKeys.PRIVATE_KEY;

        if( errors.containsKey(clientPrivateKey)){
            return errors;
        }

        // Check validity of the private key by executing the verifyPrivateKey function
        RequestConfiguration requestConfiguration = RequestConfiguration.build( contractParametersCheckRequest );
        Map<String, ContractProperty> contractProperties = requestConfiguration.getContractConfiguration().getContractProperties();
        contractProperties.put( clientPrivateKey, new ContractProperty( accountInfo.get( clientPrivateKey ) ) );


        try {
            // Try to retrieve an access token
            sharegroopHttpClient.verifyPrivateKey(requestConfiguration);
        }
        catch( PluginException e ){
            // If an exception is thrown, it means that the clientprivate key is wrong
            errors.put( clientPrivateKey, e.getErrorCode() );
        }

        return errors;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Override
    public ReleaseInformation getReleaseInformation() {
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate( LocalDate.parse(releaseProperties.get("release.date"), DateTimeFormatter.ofPattern("dd/MM/yyyy")) )
                .withVersion( releaseProperties.get("release.version") )
                .build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Override
    public String getName(Locale locale) {
        return i18n.getMessage("paymentMethod.name", locale);
    }

}
