package com.payline.payment.sharegroop.exception;

import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PluginExceptionTest {

    private static final Exception CAUSE = new Exception("this is the cause");
    private static final String LONG_MESSAGE = "This message is longer than the max length authorized for an errorCode";

    private PluginException exception;
    /**------------------------------------------------------------------------------------------------------------------*/
    @BeforeEach
    void setup(){
        this.exception = new PluginException(LONG_MESSAGE, FailureCause.COMMUNICATION_ERROR, CAUSE);
    }

    private static Stream<Arguments> constructorsTestSet() {
        return Stream.of(
                Arguments.of( new PluginException(LONG_MESSAGE) ),
                Arguments.of( new PluginException(LONG_MESSAGE, FailureCause.COMMUNICATION_ERROR) ),
                Arguments.of( new PluginException(LONG_MESSAGE), CAUSE ),
                Arguments.of( new PluginException(LONG_MESSAGE), FailureCause.COMMUNICATION_ERROR, CAUSE )
        );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @ParameterizedTest
    @MethodSource("constructorsTestSet")
    void constructors( PluginException exception ){
        // Assert the exception has a message
        assertNotNull( exception.getMessage() );

        // Assert the exception has a failure cause and an error code
        assertNotNull( exception.getErrorCode() );
        assertNotNull( exception.getFailureCause() );

        // Assert the error code is no longer than the maximum authorized length (@see development best practices on Confluence)
        assertTrue( exception.getErrorCode().length() <= PluginException.ERROR_CODE_MAX_LENGTH );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void constructorExceptions(){
        // Expected an exception when calling the constructor with a null message or failure cause
        assertThrows( IllegalStateException.class, () -> new PluginException( null ) );
        assertThrows( IllegalStateException.class, () -> new PluginException( null, FailureCause.COMMUNICATION_ERROR ) );
        assertThrows( IllegalStateException.class, () -> new PluginException( LONG_MESSAGE, (FailureCause) null ) );
        assertThrows( IllegalStateException.class, () -> new PluginException( null, CAUSE ) );
        assertThrows( IllegalStateException.class, () -> new PluginException( null, FailureCause.COMMUNICATION_ERROR, CAUSE ) );
        assertThrows( IllegalStateException.class, () -> new PluginException( LONG_MESSAGE, null, CAUSE ) );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void toPaymentResponseFailure(){
        // when converting the exception to a PaymentResponseFailure
        PaymentResponseFailure prf = this.exception.toPaymentResponseFailureBuilder().build();

        // expect the object elements to be valid
        assertNotNull( prf.getErrorCode() );
        assertTrue( prf.getErrorCode().length() <= PluginException.ERROR_CODE_MAX_LENGTH );
        assertNotNull( prf.getFailureCause() );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void toPaymentFormConfigurationResponseFailure(){
        // when converting the exception to a PaymentResponseFailure
        PaymentFormConfigurationResponseFailure pfcrf = this.exception.toPaymentFormConfigurationResponseFailureBuilder().build();

        // expect the object elements to be valid
        assertNotNull( pfcrf.getErrorCode() );
        assertTrue( pfcrf.getErrorCode().length() <= PluginException.ERROR_CODE_MAX_LENGTH );
        assertNotNull( pfcrf.getFailureCause() );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void toRefundResponseFailure(){
        // when converting the exception to a PaymentResponseFailure
        RefundResponseFailure rrf = this.exception.toRefundResponseFailureBuilder().build();

        // expect the object elements to be valid
        assertNotNull( rrf.getErrorCode() );
        assertTrue( rrf.getErrorCode().length() <= PluginException.ERROR_CODE_MAX_LENGTH );
        assertNotNull( rrf.getFailureCause() );
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Test
    void runtimeErrorCode(){
        // A NullPointerException is thrown
        String errorCode = null;
        String str = null;
        try {
            str.equals("toto");
        }
        catch( RuntimeException e ){
            errorCode = PluginException.runtimeErrorCode( e );
        }

        // the message is null, so the error code contains the exception class name
        assertEquals("plugin error: NullPointerException", errorCode);
    }
    /**------------------------------------------------------------------------------------------------------------------*/

}
