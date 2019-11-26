package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseFailure;
import com.payline.pmapi.bean.reset.response.impl.ResetResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ResetService;
import org.apache.logging.log4j.Logger;

public class ResetServiceImpl implements ResetService {

    private static final Logger LOGGER = LogManager.getLogger(ResetServiceImpl.class);

    private SharegroopHttpClient httpClient = SharegroopHttpClient.getInstance();

    @Override
    public ResetResponse resetRequest(ResetRequest resetRequest) {
        try {
            RequestConfiguration requestConfiguration = new RequestConfiguration(resetRequest.getContractConfiguration(), resetRequest.getEnvironment(), resetRequest.getPartnerConfiguration());
            SharegroopAPICallResponse sharegroopAPICallResponse = httpClient.cancelOrder(requestConfiguration, resetRequest.getTransactionId());

            if (sharegroopAPICallResponse.getSuccess()) {
                sharegroopAPICallResponse = httpClient.verifyOrder(requestConfiguration,resetRequest.getTransactionId());
                if (sharegroopAPICallResponse.getSuccess() && "refunded".equalsIgnoreCase(sharegroopAPICallResponse.getData().getStatus())) {
                    return createResponseSuccess(sharegroopAPICallResponse);
                }else{
                    return createResponseFailure(sharegroopAPICallResponse);
                }
            }else{
                return createResponseFailure(sharegroopAPICallResponse);
            }


        } catch (PluginException e) {
            return e.toResetResponseFailureBuilder().build();
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            return ResetResponseFailure.ResetResponseFailureBuilder.aResetResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }

    @Override
    public boolean canMultiple() {
        return false;
    }

    @Override
    public boolean canPartial() {
        return false;
    }

    /**------------------------------------------------------------------------------------------------------------------*/
    private ResetResponseFailure createResponseFailure(SharegroopAPICallResponse response) {
        return ResetResponseFailure.ResetResponseFailureBuilder
                .aResetResponseFailure()
                .withPartnerTransactionId(response.getData().getId())
                .withErrorCode(response.getStatus())
                .withFailureCause(FailureCause.INVALID_DATA)
                .build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    private ResetResponseSuccess createResponseSuccess(SharegroopAPICallResponse response) {
        return ResetResponseSuccess.ResetResponseSuccessBuilder
                .aResetResponseSuccess()
                .withPartnerTransactionId(response.getData().getId())
                .withStatusCode(response.getStatus())
                .build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
}
