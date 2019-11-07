package com.payline.payment.sharegroop.utils;

/**
 * Support for constants used everywhere in the plugin sources.
 */
public class Constants {

    /**
     * Keys for the entries in ContractConfiguration map.
     */
    public static class ContractConfigurationKeys {

        public static final String PUBLIC_KEY = "PUBLIC_KEY";
        public static final String PRIVATE_KEY = "PRIVATE_KEY";
        public static final String UX = "UX";
        public static final String SECURE_3D = "SECURE_3D";



        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private ContractConfigurationKeys(){}
    }

    /**
     * Keys for the entries in PartnerConfiguration maps.
     */
    public static class PartnerConfigurationKeys {

        public static final String SHAREGROOP_URL = "SHAREGROOP_URL";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private PartnerConfigurationKeys(){}
    }


    /**
     * Keys for the entries in RequestContext data.
     */
    public static class RequestContextKeys {

        public static final String PAYMENT_ID = "paymentId";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private RequestContextKeys(){}
    }

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private Constants(){}

}
