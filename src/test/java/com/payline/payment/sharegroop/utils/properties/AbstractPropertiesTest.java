package com.payline.payment.sharegroop.utils.properties;

import com.payline.payment.sharegroop.exception.PluginException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractPropertiesTest {

    private static class NullFilenameProperties extends AbstractProperties {
        NullFilenameProperties(){}

        @Override
        String getFilename() {
            return null;
        }
    }

    @Test
    void readProperties_nullFilename(){
        // given the getFilename method implementation returns null
        // when calling the constructor, an exception is thrown
        assertThrows( PluginException.class, NullFilenameProperties::new );
    }

    private static class EmptyFilenameProperties extends AbstractProperties {
        EmptyFilenameProperties() {}

        @Override
        String getFilename() {
            return "";
        }
    }

    @Test
    void readProperties_emptyFilename(){
        // given the getFilename method implementation returns an empty string
        // when calling the constructor, an exception is thrown
        assertThrows( PluginException.class, EmptyFilenameProperties::new );
    }

    private static class NonExistingFilenameProperties extends AbstractProperties {
        NonExistingFilenameProperties() {}

        @Override
        String getFilename() {
            return "does_not_exist.properties";
        }
    }

    @Test
    void readProperties_nonExistingFile(){
        // given the getFilename method implementation returns a filename that does not exist
        // when calling the constructor, an exception is thrown
        assertThrows( PluginException.class, NonExistingFilenameProperties::new );
    }

}
