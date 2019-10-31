import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.bean.payment.Orders;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.payment.sharegroop.utils.http.StringResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainTester {

    private static SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();
    /**------------------------------------------------------------------------------------------------------------------*/
    public static void main(String[] args) throws IOException {
        Orders orders = new Orders();

        try {

            PartnerConfiguration partnerConfiguration = initPartnerConfiguration();
            RequestConfiguration requestConfiguration = new RequestConfiguration(initContractConfiguration(), MockUtils.anEnvironment(), partnerConfiguration);

            sharegroopHttpClient.init(partnerConfiguration);
            StringResponse response = sharegroopHttpClient.verifyConection(requestConfiguration);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private static PartnerConfiguration initPartnerConfiguration() throws IOException {

        Map<String, String> partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.SHAREGROOP_URL_SANDBOX, "https://api.sandbox.sharegroop.com");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration( partnerConfigurationMap, sensitiveConfigurationMap );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private static ContractConfiguration initContractConfiguration(){

        ContractConfiguration contractConfiguration = new ContractConfiguration("Sharegroop", new HashMap<>());

        contractConfiguration.getContractProperties().put(Constants.ContractConfigurationKeys.PRIVATE_KEY, new ContractProperty("sk_test_fb1ec051-4d3a-4f0e-a9dd-40141013e324"));

        return contractConfiguration;
    }
}
