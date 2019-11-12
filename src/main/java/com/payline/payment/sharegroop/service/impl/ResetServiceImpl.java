package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ResetService;
import org.apache.logging.log4j.Logger;

public class ResetServiceImpl implements ResetService {

    private static final Logger LOGGER = LogManager.getLogger(ResetServiceImpl.class);

    private SharegroopHttpClient httpClient = SharegroopHttpClient.getInstance();

    @Override
    public ResetResponse resetRequest(ResetRequest resetRequest) {
        ResetResponse resetResponse;
        try {
            // TODO: implement
            resetResponse = null;
        }
        catch( PluginException e ){
            resetResponse = e.toResetResponseFailureBuilder().build();
        }
        catch( RuntimeException e ){
            LOGGER.error("Unexpected plugin error", e);
            resetResponse = ResetResponseFailure.ResetResponseFailureBuilder.aResetResponseFailure()
                    .withErrorCode( PluginException.runtimeErrorCode( e ) )
                    .withFailureCause( FailureCause.INTERNAL_ERROR )
                    .build();
        }

        return resetResponse;
    }

    @Override
    public boolean canMultiple() {
        // TODO: à vérifier !
        return false;
    }

    @Override
    public boolean canPartial() {
        // TODO: à vérifier !
        return false;
    }
}
