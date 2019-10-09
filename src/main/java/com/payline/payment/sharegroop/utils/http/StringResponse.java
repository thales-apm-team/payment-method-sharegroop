package com.payline.payment.sharegroop.utils.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple POJO supporting the core elements of an HTTP response, in a more readable format (especially the content).
 */
public class StringResponse {

    private String content;
    private Map<String, String> headers;
    private int statusCode;
    private String statusMessage;

    public String getContent() {
        return content;
    }

    public String getHeader(String name){
        if( headers != null && name != null ){
            return headers.get( name.toLowerCase() );
        }
        return null;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean isSuccess(){
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Safely extract the elements of a {@link StringResponse} from a {@link HttpResponse}.
     * @param httpResponse the HTTP response
     * @return The corresponding StringResponse, or null if the input cannot be read or contains incomplete data.
     */
    public static StringResponse fromHttpResponse(HttpResponse httpResponse) {
        StringResponse instance = null;

        if( httpResponse != null && httpResponse.getStatusLine() != null ){
            instance = new StringResponse();
            instance.statusCode = httpResponse.getStatusLine().getStatusCode();
            instance.statusMessage = httpResponse.getStatusLine().getReasonPhrase();

            try {
                instance.content = EntityUtils.toString(httpResponse.getEntity());
            } catch (IOException e) {
                instance.content = null;
            }

            instance.headers = new HashMap<>();
            Header[] rawHeaders = httpResponse.getAllHeaders();
            for( int i=0; i<rawHeaders.length; i++ ){
                instance.headers.put( rawHeaders[i].getName().toLowerCase(), rawHeaders[i].getValue() );
            }
        }

        return instance;
    }

}
