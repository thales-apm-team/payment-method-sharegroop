package com.payline.payment.sharegroop;

import com.payline.pmapi.bean.payment.Environment;

public class MockUtils {
    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment(){
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }

}
