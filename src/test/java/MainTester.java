import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.bean.payment.Data;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainTester {
    private static final Logger LOGGER = LogManager.getLogger(MainTester.class);
    private static SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();
    /**------------------------------------------------------------------------------------------------------------------*/
    public static void main(String[] args) throws IOException {
        Data data = new Data();

        try {

            RequestConfiguration requestConfiguration = new RequestConfiguration(initContractConfiguration(), MockUtils.anEnvironment(), MockUtils.aPartnerConfiguration());

            // Test : VerifyPrivateKey
             sharegroopHttpClient.verifyPrivateKey(requestConfiguration);


            // Test : CreateOrder
            data = sharegroopHttpClient.createOrder(requestConfiguration,MockUtils.anOrder());
            LOGGER.info("Order data : " + data);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private static PartnerConfiguration initPartnerConfiguration() throws IOException {

        Map<String, String> partnerConfigurationMap = new HashMap<>();


        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL, "https://api.sandbox.sharegroop.com");
        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration( partnerConfigurationMap, sensitiveConfigurationMap );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private static ContractConfiguration initContractConfiguration(){

        ContractConfiguration contractConfiguration = new ContractConfiguration("Sharegroop", new HashMap<>());
        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty( System.getProperty("project.clientPrivateKey")));

        return contractConfiguration;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
}
