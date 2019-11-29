package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.RefundService;
import org.apache.logging.log4j.Logger;

public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LogManager.getLogger(RefundServiceImpl.class);
    private SharegroopHttpClient httpClient = SharegroopHttpClient.getInstance();

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        try {
            RequestConfiguration requestConfiguration = new RequestConfiguration(refundRequest.getContractConfiguration(), refundRequest.getEnvironment(), refundRequest.getPartnerConfiguration());
            SharegroopAPICallResponse sharegroopAPICallResponse = httpClient.refundOrder(requestConfiguration, refundRequest.getTransactionId());

            if (sharegroopAPICallResponse.getSuccess() && "refunded".equalsIgnoreCase(sharegroopAPICallResponse.getData().getStatus())) {
                return RefundResponseSuccess.RefundResponseSuccessBuilder
                        .aRefundResponseSuccess()
                        .withPartnerTransactionId(sharegroopAPICallResponse.getData().getId())
                        .withStatusCode(sharegroopAPICallResponse.getStatus())
                        .build();
            }
            return RefundResponseFailure.RefundResponseFailureBuilder
                    .aRefundResponseFailure()
                    .withPartnerTransactionId(sharegroopAPICallResponse.getData().getId())
                    .withErrorCode(sharegroopAPICallResponse.getStatus())
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        } catch (PluginException e) {
            return e.toRefundResponseFailureBuilder().build();
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
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

}
