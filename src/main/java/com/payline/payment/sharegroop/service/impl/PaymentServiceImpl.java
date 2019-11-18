package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.bean.JsResponse;
import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.exception.InvalidDataException;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.PluginUtils;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.Order;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.Email;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.paymentform.bean.form.PartnerWidgetForm;
import com.payline.pmapi.bean.paymentform.bean.form.partnerwidget.*;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PaymentServiceImpl implements PaymentService {
    private static final String DATA = "data";

    private static final String SHAREGROOP_URL = "https://widget.sandbox.sharegroop.com/widget.js";
    private static final String DIV_ID = "#sharegroopPaymentForm";
    private static final String CALLBACK_NAME = "paylineProcessPaymentCallback";
    private static final String CONTEXT_DATA_STEP = "STEP";
    private static final String CONTEXT_DATA_ORDER = "ORDER";
    private static final String CONTEXT_DATA_EMAIL = "EMAIL";
    private static final String CONTEXT_DATA_STATUS = "STATUS";
    private static final String STEP1_DESCRIPTION = "Captain init window";
    private static final String STEP2_DESCRIPTION = "Share link window";
    private static final String STEP2 = "STEP2";
    private static final String STEP3 = "STEP3";

    private static final String SELECTOR = "[SELECTOR]";
    private static final String PUBLIC_KEY = "[PUBLIC_KEY]";
    private static final String LOCALE = "[LOCALE]";
    private static final String ORDER_ID = "[ORDER_ID]";

    private static final String CURRENCY = "[CURRENCY]";
    private static final String AMOUNT = "[AMOUNT]";

    private static final String EMAIL = "[EMAIL]";
    private static final String UX = "[UX]";
    private static final String FIRSTNAME = "[FIRSTNAME]";
    private static final String LASTNAME = "[LASTNAME]";
    private static final String TRACK_ID = "[TRACK_ID]";

    private static final String ITEMS = "[ITEMS]";
    private static final String ITEM_TRACK_ID = "[ITEM_TRACK_ID]";
    private static final String ITEM_AMOUNT = "[ITEM_AMOUNT]";
    private static final String ITEM_QUANTITY = "[ITEM_QUANTITY]";

    private static final String CALLBACK = "[CALLBACK]";

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();


    public PaymentServiceImpl() {
    }

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {

        try {
            // get the step
            String step = paymentRequest.getRequestContext().getRequestData().get(CONTEXT_DATA_STEP);
            if (step == null || step.equals("")) {
                // first step, we've got to return the "captain init" js
                return step1(paymentRequest);
            } else if (STEP2.equalsIgnoreCase(step)) {
                // second step, an API call to verify the transaction status is needed
                // if the response is OK and the status is "authorized" or "captured", then return the "share link" js
                return step2(paymentRequest);
            } else if (STEP3.equalsIgnoreCase(step)) {
                // third step, the captain has received the share link
                // we just need to return a success;
                return step3(paymentRequest);
            } else {
                // should never append
                String errorMessage = "Unknown step";
                LOGGER.error(errorMessage);
                return PaymentResponseFailure.PaymentResponseFailureBuilder
                        .aPaymentResponseFailure()
                        .withErrorCode(errorMessage)
                        .withFailureCause(FailureCause.INVALID_DATA)
                        .build();
            }
        } catch (PluginException e) {
            return e.toPaymentResponseFailureBuilder()
                    .build();
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
    }

    private String getStepOneScript(PaymentRequest request) {
        String templateScript = "ShareGroop.initCaptain({\n" +
                "        \"selector\": \"" + SELECTOR + "\",\n" +
                "        \"publicKey\": \"" + PUBLIC_KEY + "\",\n" +
                "        \"locale\": \"" + LOCALE + "\",\n" +
                "        \"currency\": \"" + CURRENCY + "\",\n" +
                "        \"order\": {\n" +
                "           \"email\": \"" + EMAIL + "\",\n" +
                "           \"ux\": \"" + UX + "\"" +
                "           \"firstName\": \"" + FIRSTNAME + "\",\n" +
                "           \"lastName\": \"" + LASTNAME + "\",\n" +
                "           \"trackId\": \"" + TRACK_ID + "\",\n" +
                "           \"amount\": " + AMOUNT + ",\n" +
                "           \"items\": [ " + ITEMS + " ]\n" +
                "        },\n" +
                "        \"events\": {\n" +
                "          \"onValidated\": function(data) { " + CALLBACK + "(data); }\n" +
                "          \"onInvalid\": function () {     "+CALLBACK+"(); }," +
                "          \"onError\": function () {     "+CALLBACK+"(); }," +
                "        }\n" +
                "    }).mount();";

        String templateItem = "{\n" +
                "               \"trackId\": \"" + ITEM_TRACK_ID + "\",\n" +
                "               \"amount\": " + ITEM_AMOUNT + ",\n" +
                "               \"quantity\": " + ITEM_QUANTITY + "\n" +
                "             }\n";


        // create the list of items
        List<String> itemList = new ArrayList<>();
        for (Order.OrderItem i : request.getOrder().getItems()) {
            String item = templateItem
                    .replace(ITEM_TRACK_ID, i.getReference())
                    .replace(ITEM_AMOUNT, i.getAmount().getAmountInSmallestUnit().toString())
                    .replace(ITEM_QUANTITY, i.getQuantity().toString());

            itemList.add(item);
        }
        String items = String.join(",", itemList);

        // create Script with good values
        return templateScript
                .replace(SELECTOR, DIV_ID)
                .replace(PUBLIC_KEY, request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PUBLIC_KEY).getValue())
                .replace(LOCALE, request.getLocale().getCountry())
                .replace(CURRENCY, request.getAmount().getCurrency().getCurrencyCode())
                .replace(AMOUNT, request.getAmount().getAmountInSmallestUnit().toString())
                .replace(EMAIL, request.getBuyer().getEmail())
                .replace(FIRSTNAME, request.getBuyer().getFullName().getFirstName())
                .replace(TRACK_ID, request.getBuyer().getFullName().getLastName())
                .replace(ITEMS, items)
                .replace(CALLBACK, CALLBACK_NAME);
    }

    private PaymentResponse step1(PaymentRequest paymentRequest) {

        // create init form to return
        String script = getStepOneScript(paymentRequest);
        PaymentFormConfigurationResponse configurationResponse = createForm(script, STEP1_DESCRIPTION, true);

        // add step information
        Map<String, String> requestContextMap = new HashMap<>();
        requestContextMap.put(CONTEXT_DATA_STEP, STEP2);

        //Get sensitiveRequestContext from Payment request
        RequestContext requestContext = RequestContext
                .RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestContextMap)
                .build();

        return PaymentResponseFormUpdated.PaymentResponseFormUpdatedBuilder
                .aPaymentResponseFormUpdated()
                .withPaymentFormConfigurationResponse(configurationResponse)
                .withRequestContext(requestContext)
                .build();
    }

    private PaymentResponse step2(PaymentRequest request) {
        // extract js response data
        String jsonPaymentData = request.getPaymentFormContext().getPaymentFormParameter().get(DATA);
        if (jsonPaymentData == null || jsonPaymentData.length() == 0){
            String errorMessage = "An unknown error occurred during captain initialisation";
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginUtils.truncate(errorMessage, 50))
                    .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                    .build();
        }

        JsResponse jsResponse = JsResponse.fromJson(jsonPaymentData);

        // do the call
        RequestConfiguration requestConfiguration = new RequestConfiguration(request.getContractConfiguration(), request.getEnvironment(), request.getPartnerConfiguration());
        SharegroopAPICallResponse response = sharegroopHttpClient.verifyOrder(requestConfiguration, jsResponse.getOrder());

        // check the response and the status response
        if (!response.getSuccess()) {
            // return a failure
            LOGGER.error(response.getErrors().get(0));
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(jsResponse.getOrder())
                    .withErrorCode(PluginUtils.truncate(response.getErrors().get(0), 50))
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        }
        String status = response.getData().getStatus();
        if (!"authorized".equalsIgnoreCase(status) && !"captured".equalsIgnoreCase(status)) {
            // wrong payment status, return a failure
            String errorMessage = "Wrong transaction status: " + status;
            LOGGER.error(errorMessage);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(jsResponse.getOrder())
                    .withErrorCode(PluginUtils.truncate(errorMessage, 50))
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        }


        // return the share link js
        String script = getStepThreeScript(request, jsResponse.getOrder());
        PaymentFormConfigurationResponse configurationResponse = createForm(script, STEP2_DESCRIPTION, false);

        // add step and payment information
        Map<String, String> requestContextMap = new HashMap<>();
        requestContextMap.put(CONTEXT_DATA_STEP, STEP3);
        requestContextMap.put(CONTEXT_DATA_EMAIL, jsResponse.getEmail());
        requestContextMap.put(CONTEXT_DATA_ORDER, jsResponse.getOrder());
        requestContextMap.put(CONTEXT_DATA_STATUS, jsResponse.getStatus());

        RequestContext requestContext = RequestContext
                .RequestContextBuilder
                .aRequestContext()
                .withRequestData(requestContextMap)
                .build();

        return PaymentResponseFormUpdated.PaymentResponseFormUpdatedBuilder
                .aPaymentResponseFormUpdated()
                .withPaymentFormConfigurationResponse(configurationResponse)
                .withRequestContext(requestContext)
                .build();
    }

    private String getStepThreeScript(PaymentRequest request, String partnerTransactionId) {
        String templateScript = "ShareGroop.initLink({\n" +
                "\"selector\": \"" + SELECTOR + "\",\n" +
                "\"publicKey\": \"" + PUBLIC_KEY + "\",\n" +
                "\"orderId\": \"" + ORDER_ID + "\",\n" +
                "\"locale\": \"" + LOCALE + "\",\n" +
                "\"events\": {\n" +
                "  \"onReady\": function () {  "+CALLBACK+"(); }," +
                "}\n" +
                "});";

        return templateScript
                .replace(SELECTOR, DIV_ID)
                .replace(PUBLIC_KEY, request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.PUBLIC_KEY).getValue())
                .replace(ORDER_ID, partnerTransactionId)
                .replace(LOCALE, request.getLocale().getCountry())
                .replace(CALLBACK, CALLBACK_NAME);
    }

    private PaymentResponse step3(PaymentRequest request) {
        // get data from step2
        String partnerTransactionId = request.getRequestContext().getRequestData().get(CONTEXT_DATA_ORDER);
        String email = request.getRequestContext().getRequestData().get(CONTEXT_DATA_EMAIL);
        String status = request.getRequestContext().getRequestData().get(CONTEXT_DATA_STATUS);

        // create response data
        Email buyerId = Email.EmailBuilder
                .anEmail()
                .withEmail(email)
                .build();

        return PaymentResponseSuccess.PaymentResponseSuccessBuilder
                .aPaymentResponseSuccess()
                .withPartnerTransactionId(partnerTransactionId)
                .withStatusCode(status)
                .withTransactionDetails(buyerId)
                .build();
    }

    private PaymentFormConfigurationResponse createForm(String script, String description, boolean autoRedirect) {
        try {
            // script to import
            PartnerWidgetScriptImport scriptImport = PartnerWidgetScriptImport.WidgetPartnerScriptImportBuilder
                    .aWidgetPartnerScriptImport()
                    .withUrl(new URL(SHAREGROOP_URL))
                    .withCache(true)
                    .withAsync(true)
                    .build();

            // div that contains the script to load
            PartnerWidgetContainer container = PartnerWidgetContainerTargetDivId.WidgetPartnerContainerTargetDivIdBuilder
                    .aWidgetPartnerContainerTargetDivId()
                    .withId(DIV_ID)
                    .build();

            // method to call when payment is done (in the "onValidated" event)
            PartnerWidgetOnPay onPay = PartnerWidgetOnPayCallBack.WidgetContainerOnPayCallBackBuilder
                    .aWidgetContainerOnPayCallBack()
                    .withName(CALLBACK_NAME)
                    .build();

            PartnerWidgetForm widgetForm = PartnerWidgetForm.WidgetPartnerFormBuilder
                    .aWidgetPartnerForm()
                    .withDescription(description)
                    .withScriptImport(scriptImport)
                    .withLoadingScriptAfterImport(script)
                    .withContainer(container)
                    .withOnPay(onPay)
                    .withPerformsAutomaticRedirection(autoRedirect)
                    .build();

            return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                    .aPaymentFormConfigurationResponseSpecific()
                    .withPaymentForm(widgetForm)
                    .build();
        } catch (MalformedURLException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }
}