
import com.payline.payment.sharegroop.bean.TemplateRequest;
import com.payline.payment.sharegroop.bean.payment.Orders;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;

public class MainTest {

    private static SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();

    public static void main( String[] args ) throws IOException {
        Orders  orders = new Orders();

        try {

        orders = sharegroopHttpClient.verifyConection();

        } catch( Exception e ){
            e.printStackTrace();
        }

    }
}
