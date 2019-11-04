package com.payline.payment.sharegroop.utils.http;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.mockito.internal.util.reflection.FieldSetter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

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
    /**
     * Moch a StringResponse with the given elements.
     *
     * @param statusCode The HTTP status code (ex: 200, 403)
     * @param statusMessage The HTTP status message (ex: "OK", "Forbidden")
     * @param content The response content as a string
     * @param headers The response headers
     * @return A mocked StringResponse
     */
    public static StringResponse mockStringResponse( int statusCode, String statusMessage, String content, Map<String, String> headers ){
        StringResponse response = new StringResponse();

        try {
            if (content != null && !content.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("content"), content);
            }
            if (headers != null && headers.size() > 0) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("headers"), headers);
            }
            if (statusCode >= 100 && statusCode < 600) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusCode"), statusCode);
            }
            if (statusMessage != null && !statusMessage.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusMessage"), statusMessage);
            }
        }
        catch( NoSuchFieldException e ){
            // This would happen in a testing context: spare the exception throw, the test case will probably fail anyway
            return null;
        }

        return response;
    }
}
