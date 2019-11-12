import com.payline.payment.sharegroop.MockUtils;
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


public class MainTester {
    private static final Logger LOGGER = LogManager.getLogger(MainTester.class);
    private static SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();
    /**------------------------------------------------------------------------------------------------------------------*/
    public static void main(String[] args) throws IOException {
        Data dataCreateOrder;
        Data dataVerifyOrder;
        Data dataRefundOrder;
        Data dataCancelOrder;
        Boolean privateKeyStatus;


        try {

            RequestConfiguration requestConfiguration = new RequestConfiguration(initContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

            // Test : VerifyPrivateKey
            privateKeyStatus = sharegroopHttpClient.verifyPrivateKey(requestConfiguration);
             LOGGER.info("Private Key Status : " + privateKeyStatus);
            // Test : CreateOrder
            dataCreateOrder = sharegroopHttpClient.createOrder(requestConfiguration,MockUtils.anOrder());
            LOGGER.info("Data Create Order : " + dataCreateOrder);

            // Test : Verify
            dataVerifyOrder = sharegroopHttpClient.verifyOrder(requestConfiguration, dataCreateOrder.getId());
            LOGGER.info("Data Verify Order : " + dataVerifyOrder);

            // Test Refund
           /* dataRefundOrder = sharegroopHttpClient.refundOrder(requestConfiguration,"ord_5b86bc07-3ba6-4fde-a19c-807f95045415");
            LOGGER.info("Data Refund Order" + dataRefundOrder);*/

            // Test Cancel
            dataCancelOrder = sharegroopHttpClient.cancelOrder(requestConfiguration,"ord_f2aa10bf-aa39-4431-b9a9-273c5e81a71b");
            LOGGER.info("Data Cancel Order" + dataCancelOrder);


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
