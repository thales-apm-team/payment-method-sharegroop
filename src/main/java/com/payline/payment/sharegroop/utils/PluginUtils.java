package com.payline.payment.sharegroop.utils;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;

import java.text.Normalizer;
import java.util.*;

public class PluginUtils {

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private PluginUtils(){}

    /**
     * Add a amount of time to the given date.
     *
     * @param to the date
     * @param field the type of time unit to add (see {@link Calendar} constants)
     * @param n The number of this time unit to add. Can be negative.
     * @return the new date
     */
    public static Date addTime(Date to, int field, int n ){
        Calendar cal = Calendar.getInstance();
        cal.setTime( to );
        cal.add(field, n);
        return cal.getTime();
    }

    public static String jsonMinify( String json ){
        return json.trim()
                // remove line breaks and tabulations
                .replaceAll("[\\n\\r\\t]+\\s*", "")
                // remove spaces around properties name/value colon separator
                .replaceAll("\"\\s*:\\s*([{\\[\"]+)", "\":$1")
                // remove spaces around properties separator
                .replaceAll("([\"\\]}]+)\\s*,\\s*([\"\\[{]+)", "$1,$2")
                // remove spaces between closing brackets
                .replaceAll("([}\\]]+)\\s+([}\\]]+)", "$1$2");
    }

    /**
     * Replace all accentuated characters in the given string by their non-accentuated form.
     * Replace also every character not supported by the partner API (see below) by a space or "?".
     *
     * Supported characters :
     * - letters (upper or lower cases)
     * - numbers
     * - spaces
     * - special characters / - ? : ( ) . , ‟ +
     *
     * @param input the string to process
     * @return the string with only supported characters
     */
    public static String replaceChars( String input ){
        if( input == null ){
            return null;
        }
        input = input.replace("'", " ")
                .replace("æ", "ae")
                .replace("Æ", "AE")
                .replace("œ", "oe")
                .replace("Œ", "OE");
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        input = input.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return input;
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

        return str;
    }

    /**
     * Put an entry into the given map only if the given key and value are not null.
     *
     * @param map The map
     * @param key The wanted key for the new entry
     * @param value The wanted value for the new entry
     */
    public static void safePut(Map<String, String> map, String key, String value ){
        if( key != null && value != null ){
            map.put( key, value );
        }
    }

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

}