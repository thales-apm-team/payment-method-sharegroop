package com.payline.payment.sharegroop.utils.http;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Utility class for test purpose related to HTTP calls.
 */

public class HttpTestUtils {
/**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Mock an HTTP Response with the given elements.
     *
     * @param statusCode The HTTP status code (ex: 200)
     * @param statusMessage The HTTP status message (ex: "OK")
     * @param content The response content/body
     * @return A mocked HTTP response
     */
    static CloseableHttpResponse mockHttpResponse(int statusCode, String statusMessage, String content, Header[] headers ){
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        doReturn( new BasicStatusLine( new ProtocolVersion("HTTP", 1, 1), statusCode, statusMessage) )
                .when( response ).getStatusLine();
        doReturn( new StringEntity( content, StandardCharsets.UTF_8 ) ).when( response ).getEntity();
        if( headers != null && headers.length >= 1 ){
            doReturn( headers ).when( response ).getAllHeaders();
        } else {
            doReturn( new Header[]{} ).when( response ).getAllHeaders();
        }
        return response;
    }
/**------------------------------------------------------------------------------------------------------------------*/
}
