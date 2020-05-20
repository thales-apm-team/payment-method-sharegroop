package com.payline.payment.sharegroop.utils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PluginUtilsTest {

    @Test
    void requestToString_get(){
        // given: a HTTP request with headers
        HttpGet request = new HttpGet( "http://domain.test.fr/endpoint" );
        request.setHeader("Authorization", "Basic sensitiveStringThatShouldNotAppear");
        request.setHeader("Other", "This is safe to display");

        // when: converting the request to String for display
        String result = PluginUtils.requestToString( request );

        // then: the result is as expected
        String ln = System.lineSeparator();
        String expected = "GET http://domain.test.fr/endpoint" + ln
                + "Authorization: Basic *****" + ln
                + "Other: This is safe to display";
        assertEquals(expected, result);
    }

    @Test
    void requestToString_post(){
        // given: a HTTP request with headers
        HttpPost request = new HttpPost( "http://domain.test.fr/endpoint" );
        request.setHeader("SomeHeader", "Header value");
        request.setEntity( new StringEntity( "{\"name\":\"Jean Martin\",\"age\":\"28\"}", StandardCharsets.UTF_8 ));

        // when: converting the request to String for display
        String result = PluginUtils.requestToString( request );

        // then: the result is as expected
        String ln = System.lineSeparator();
        String expected = "POST http://domain.test.fr/endpoint" + ln
                + "SomeHeader: Header value" + ln
                + "{\"name\":\"Jean Martin\",\"age\":\"28\"}";
        assertEquals(expected, result);
    }

    @Test
    void truncate() {
        assertEquals("0123456789", PluginUtils.truncate("01234567890123456789", 10));
        assertEquals("01234567890123456789", PluginUtils.truncate("01234567890123456789", 60));
        assertEquals("", PluginUtils.truncate("", 30));
        assertNull(PluginUtils.truncate(null, 30));
    }

}
