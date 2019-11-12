package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.RefundService;
import org.apache.logging.log4j.Logger;

public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LogManager.getLogger(RefundServiceImpl.class);

    private SharegroopHttpClient httpClient = SharegroopHttpClient.getInstance();

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        RefundResponse refundResponse;
        try {
            // TODO: implement
            refundResponse = null;
        }
        catch( PluginException e ){
            refundResponse = e.toRefundResponseFailureBuilder().build();
        }
        catch( RuntimeException e ){
            LOGGER.error("Unexpected plugin error", e);
            refundResponse = RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                    .withErrorCode( PluginException.runtimeErrorCode( e ) )
                    .withFailureCause( FailureCause.INTERNAL_ERROR )
                    .build();
        }

        return refundResponse;
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
