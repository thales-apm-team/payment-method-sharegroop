import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.bean.payment.Data;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;


class MainTester {
    private static final Logger LOGGER = LogManager.getLogger(MainTester.class);
    private static final SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();
    /**------------------------------------------------------------------------------------------------------------------*/
    public static void main(String[] args) throws IOException {
        SharegroopAPICallResponse dataCreateOrder;
        SharegroopAPICallResponse dataVerifyOrder;
        SharegroopAPICallResponse dataRefundOrder;
        SharegroopAPICallResponse dataCancelOrder;
        Boolean privateKeyStatus;

        try {

            RequestConfiguration requestConfiguration = new RequestConfiguration(initContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

            // Test : VerifyPrivateKey
            privateKeyStatus = sharegroopHttpClient.verifyPrivateKey(requestConfiguration);
             LOGGER.info("Private Key Status : " + privateKeyStatus);

            // Test : CreateOrder
            dataCreateOrder = sharegroopHttpClient.createOrder(requestConfiguration,MockUtils.anOrder());
            LOGGER.info("Data Create Order : " + dataCreateOrder.getSuccess() + " - " + dataCreateOrder.getData());

            // Test : Verify
            dataVerifyOrder = sharegroopHttpClient.verifyOrder(requestConfiguration, dataCreateOrder.getData().getId());
            LOGGER.info("Data Verify Order : " + dataVerifyOrder.getSuccess() + " - " + dataVerifyOrder.getData());

            // Test Refund
            dataRefundOrder = sharegroopHttpClient.refundOrder(requestConfiguration,"ord_71878989-2592-48dc-a57b-dc3b5ab203ce");
            LOGGER.info("Data Refund Order : " + dataRefundOrder.getSuccess() + " - " + dataRefundOrder.getData());

            // Test Cancel
            dataCancelOrder = sharegroopHttpClient.cancelOrder(requestConfiguration,"ord_efdec8ba-aea9-4a41-ac20-1ede636cf8e5");
            LOGGER.info("Data Cancel Order : " + dataCancelOrder.getSuccess() + " - " + dataCancelOrder.getData());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private static ContractConfiguration initContractConfiguration(){

        ContractConfiguration contractConfiguration = new ContractConfiguration("Sharegroop", new HashMap<>());
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty( System.getProperty("project.clientPrivateKey")));

        return contractConfiguration;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
}
