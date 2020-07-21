package com.payline.payment.sharegroop.utils;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PluginUtils {

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private PluginUtils(){}

    /**
     * Truncate the given string with the given length, if necessary.
     *
     * @param value The string to truncate
     * @param length The maximum allowed length
     * @return The truncated string
     */
    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }


    /**
     * Convert the path and headers of a {@link HttpRequestBase} to a readable {@link String}.
     * Mainly, for debugging purpose.
     *
     * @param httpRequest the request to convert
     * @return request method, path and headers as a string
     */
    public static String requestToString( HttpRequestBase httpRequest ){
        String ln = System.lineSeparator();
        String str = httpRequest.getMethod() + " " + httpRequest.getURI() + ln;

        List<String> strHeaders = new ArrayList<>();
        for( Header h : httpRequest.getAllHeaders() ){
            // For obvious security reason, the value of Authorization header is never printed in the logs
            if( HttpHeaders.AUTHORIZATION.equals( h.getName() ) ){
                String[] value = h.getValue().split(" ");
                strHeaders.add( h.getName() + ": " + ( value.length > 1 ? value[0] : "" ) + " *****" );
            }
            else {
                strHeaders.add( h.getName() + ": " + h.getValue() );
            }
        }
        str += String.join(ln, strHeaders);

        if( httpRequest instanceof HttpPost){
            try {
                str += ln + new BufferedReader(new InputStreamReader(((HttpPost)httpRequest).getEntity().getContent()))
                        .lines()
                        .collect(Collectors.joining(ln));
            } catch (IOException e) {
                str += ln + "<< Error retrieving request body >>";
            }
        }

        return str;
    }

    /**
     * Convert an InputStream into a String
     *
     * @param stream the InputStream to convert
     * @return the converted String encoded in UTF-8
     */
    public static String inputStreamToString(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }

}