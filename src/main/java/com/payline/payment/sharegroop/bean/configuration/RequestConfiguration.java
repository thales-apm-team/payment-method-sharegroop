package com.payline.payment.sharegroop.bean.configuration;

import com.payline.payment.sharegroop.exception.InvalidDataException;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Environment;

/**
 * Generic class that supports any type of request's configuration.
 */
public class RequestConfiguration {

    private ContractConfiguration contractConfiguration;
    private Environment environment;
    private PartnerConfiguration partnerConfiguration;

    public RequestConfiguration(ContractConfiguration contractConfiguration, Environment environment, PartnerConfiguration partnerConfiguration) {
        if( contractConfiguration == null ){
            throw new InvalidDataException("Missing request contractConfiguration");
        }
        if( environment == null ){
            throw new InvalidDataException("Missing request environment");
        }
        if( partnerConfiguration == null ){
            throw new InvalidDataException("Missing request partnerConfiguration");
        }
        this.contractConfiguration = contractConfiguration;
        this.environment = environment;
        this.partnerConfiguration = partnerConfiguration;
    }

    public ContractConfiguration getContractConfiguration() {
        return contractConfiguration;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public PartnerConfiguration getPartnerConfiguration() {
        return partnerConfiguration;
    }

    public static com.payline.payment.sharegroop.bean.configuration.RequestConfiguration build(ContractParametersCheckRequest request){
        return new com.payline.payment.sharegroop.bean.configuration.RequestConfiguration( request.getContractConfiguration(), request.getEnvironment(), request.getPartnerConfiguration() );
    }

}
