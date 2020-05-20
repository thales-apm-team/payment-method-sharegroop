package com.payline.payment.sharegroop.exception;

import com.payline.pmapi.bean.common.FailureCause;

public class InvalidDataException extends PluginException {

    public InvalidDataException(String message ){
        super( message, FailureCause.INVALID_DATA );
    }

    public InvalidDataException(String message, Exception cause ){
        super( message, FailureCause.INVALID_DATA, cause );
    }

}
