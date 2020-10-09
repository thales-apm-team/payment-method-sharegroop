package com.payline.payment.sharegroop.service;

import com.payline.payment.sharegroop.MockUtils;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.i18n.I18nService;
import com.payline.payment.sharegroop.utils.properties.ConfigProperties;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

class LogoPaymentFormConfigurationServiceTest {

    /**
     * Private class used to test abstract class {@link LogoPaymentFormConfigurationService}.
     */
    private static class TestService extends LogoPaymentFormConfigurationService {
        @Override
        public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
            return null;
        }
    }

    @InjectMocks private TestService testService;

    @Mock private I18nService i18n;
    @Mock private ConfigProperties config;

    @BeforeEach
    void setup(){
        testService = new TestService();
        MockitoAnnotations.initMocks( this );
    }

    @Test
    void getPaymentFormLogo_nominal(){
        // given: the configuration is correct
        PaymentFormLogoRequest paymentFormLogoRequest = MockUtils.aPaymentFormLogoRequest();
        doReturn("64").when( config ).get("logo.height");
        doReturn("64").when( config ).get("logo.width");
        doReturn( "Natixis" ).when( i18n ).getMessage("paymentMethod.name", paymentFormLogoRequest.getLocale() );

        // when: calling method getPaymentFormLogo()
        PaymentFormLogoResponse logoResponse = testService.getPaymentFormLogo( paymentFormLogoRequest );

        // then:
        assertTrue( logoResponse instanceof PaymentFormLogoResponseFile );
        assertEquals( 64, ((PaymentFormLogoResponseFile) logoResponse).getHeight() );
        assertEquals( 64, ((PaymentFormLogoResponseFile) logoResponse).getWidth() );
        assertTrue( ((PaymentFormLogoResponseFile) logoResponse).getTitle().contains("Natixis") );
        assertTrue( ((PaymentFormLogoResponseFile) logoResponse).getAlt().contains("Natixis") );
    }

    @Test
    void getPaymentFormLogo_wrongHeight(){
        // given: the logo.height config value is incorrect (not an integer)
        PaymentFormLogoRequest paymentFormLogoRequest = MockUtils.aPaymentFormLogoRequest();
        doReturn("abc").when( config ).get("logo.height");
        doReturn("64").when( config ).get("logo.width");
        doReturn( "Natixis" ).when( i18n ).getMessage("paymentMethod.name", paymentFormLogoRequest.getLocale() );

        // when: calling method getPaymentFormLogo()
        assertThrows( PluginException.class, () -> testService.getPaymentFormLogo( paymentFormLogoRequest ) );
    }

    @Test
    void getPaymentFormLogo_wrongWidth(){
        // given: the logo.height config value is incorrect (not an integer)
        PaymentFormLogoRequest paymentFormLogoRequest = MockUtils.aPaymentFormLogoRequest();
        doReturn("64").when( config ).get("logo.height");
        doReturn("abc").when( config ).get("logo.width");
        doReturn( "Natixis" ).when( i18n ).getMessage("paymentMethod.name", paymentFormLogoRequest.getLocale() );

        // when: calling method getPaymentFormLogo()
        assertThrows( PluginException.class, () -> testService.getPaymentFormLogo( paymentFormLogoRequest ) );
    }

    @Test
    void getLogo_nominal(){
        // given: a valid configuration
        doReturn("test_logo.png").when( config ).get("logo.filename");
        doReturn("png").when( config ).get("logo.format");
        doReturn("image/png").when( config ).get("logo.contentType");

        // when: calling method getLogo()
        PaymentFormLogo paymentFormLogo = testService.getLogo( "whatever", Locale.getDefault() );

        // then:
        assertNotNull( paymentFormLogo.getContentType() );
        assertNotNull( paymentFormLogo.getFile() );
    }

    @Test
    void getLogo_wrongFilename(){
        // given: a valid configuration
        doReturn("does_not_exist.png").when( config ).get("logo.filename");
        doReturn("png").when( config ).get("logo.format");
        doReturn("image/png").when( config ).get("logo.contentType");
        Locale locale = Locale.getDefault();

        // when: calling method getLogo(), then: an exception is thrown
        assertThrows( PluginException.class, () -> testService.getLogo( "whatever", locale ) );
    }

}
