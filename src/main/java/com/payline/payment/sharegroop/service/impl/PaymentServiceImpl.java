package com.payline.payment.sharegroop.service.impl;

import com.payline.payment.sharegroop.bean.JsResponse;
import com.payline.payment.sharegroop.bean.SharegroopAPICallResponse;
import com.payline.payment.sharegroop.bean.configuration.RequestConfiguration;
import com.payline.payment.sharegroop.exception.InvalidDataException;
import com.payline.payment.sharegroop.exception.PluginException;
import com.payline.payment.sharegroop.utils.Constants;
import com.payline.payment.sharegroop.utils.PluginUtils;
import com.payline.payment.sharegroop.utils.http.SharegroopHttpClient;
import com.payline.payment.sharegroop.utils.i18n.I18nService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.Order;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.Email;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormDisplayFieldText;
import com.payline.pmapi.bean.paymentform.bean.field.PaymentFormField;
import com.payline.pmapi.bean.paymentform.bean.form.AbstractPaymentForm;
import com.payline.pmapi.bean.paymentform.bean.form.CustomForm;
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
    private static final String DIV_ID = "sharegroopPaymentForm";
    private static final String CALLBACK_NAME = "paylineProcessPaymentCallback";
    private static final String CONTEXT_DATA_STEP = "STEP";
    private static final String CONTEXT_DATA_ORDER = "ORDER";
    private static final String CONTEXT_DATA_EMAIL = "EMAIL";
    private static final String CONTEXT_DATA_STATUS = "STATUS";
    private static final String STEP1_DESCRIPTION = "step1.description";
    private static final String STEP2_DESCRIPTION = "step2.description";
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

    private static final String SHOW_MAIL_MESSAGE_FIELD = "step2.message";

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private SharegroopHttpClient sharegroopHttpClient = SharegroopHttpClient.getInstance();
    private I18nService i18n = I18nService.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {

        try {
            // get the step
            String step = paymentRequest.getRequestContext().getRequestData().get(CONTEXT_DATA_STEP);
            if (step == null || step.equals("")) {
                // first step, we've got to return the "captain init" js
                return step1(paymentRequest);
            } else if (STEP2.equalsIgnoreCase(step)) {
                // second step, show a message to the buyer to see his mail to get the link

                return step2(paymentRequest);
            } else if (STEP3.equalsIgnoreCase(step)) {
                // second step, an API call to verify the transaction status is needed
                // if the response is OK and the status is "authorized" or "captured", then return a successResponse
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

    private String getScript(PaymentRequest request) {
        String templateScript = "ShareGroop.initCaptain({\n" +
                "        \"selector\": \"#" + SELECTOR + "\",\n" +
                "        \"publicKey\": \"" + PUBLIC_KEY + "\",\n" +
                "        \"locale\": \"" + LOCALE + "\",\n" +
                "        \"currency\": \"" + CURRENCY + "\",\n" +
                "        \"order\": {\n" +
                "           \"email\": \"" + EMAIL + "\",\n" +
                "           \"ux\": \"" + UX + "\"," +
                "           \"firstName\": \"" + FIRSTNAME + "\",\n" +
                "           \"lastName\": \"" + LASTNAME + "\",\n" +
                "           \"trackId\": \"" + TRACK_ID + "\",\n" +
                "           \"amount\": " + AMOUNT + ",\n" +
                "           \"items\": [ " + ITEMS + " ]\n" +
                "        },\n" +
                "        \"events\": {\n" +
                "          \"onValidated\": function(data) { " + CALLBACK + "(data); },\n" +
                "          \"onInvalid\": function () {     " + CALLBACK + "(); },\n" +
                "          \"onError\": function () {     " + CALLBACK + "(); }\n" +
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
                .replace(LOCALE, request.getLocale().getLanguage())
                .replace(CURRENCY, request.getAmount().getCurrency().getCurrencyCode())
                .replace(AMOUNT, request.getAmount().getAmountInSmallestUnit().toString())
                .replace(EMAIL, request.getBuyer().getEmail())
                .replace(FIRSTNAME, request.getBuyer().getFullName().getFirstName())
                .replace(LASTNAME, request.getBuyer().getFullName().getLastName())
                .replace(TRACK_ID, request.getTransactionId())
                .replace(UX, request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.UX).getValue())
                .replace(ITEMS, items)
                .replace(CALLBACK, CALLBACK_NAME);
    }

    private PaymentResponse step1(PaymentRequest paymentRequest) {

        // create init form to return
        String script = getScript(paymentRequest);
        PaymentFormConfigurationResponse configurationResponse = createForm(script);

        // add step information
        Map<String, String> requestContextMap = new HashMap<>();
        requestContextMap.put(CONTEXT_DATA_STEP, STEP2);
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
        if (jsonPaymentData == null || jsonPaymentData.length() == 0) {
            String errorMessage = "An unknown error occurred during captain initialisation";
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginUtils.truncate(errorMessage, 50))
                    .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR)
                    .build();
        }
        JsResponse jsResponse = JsResponse.fromJson(jsonPaymentData);

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


        List<PaymentFormField> customFields = new ArrayList<>();
        PaymentFormField field = PaymentFormDisplayFieldText.PaymentFormDisplayFieldTextBuilder
                .aPaymentFormDisplayFieldText()
                .withContent( i18n.getMessage(SHOW_MAIL_MESSAGE_FIELD, request.getLocale())  )
                .build();
        customFields.add(field);

        AbstractPaymentForm form = CustomForm.builder()
                .withDisplayButton(false)
                .withDescription(i18n.getMessage(STEP2_DESCRIPTION, request.getLocale())  )
                .withCustomFields(customFields)
                .build();

        PaymentFormConfigurationResponse paymentFormConfigurationResponse = PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(form)
                .build();

        return PaymentResponseFormUpdated.PaymentResponseFormUpdatedBuilder
                .aPaymentResponseFormUpdated()
                .withPaymentFormConfigurationResponse(paymentFormConfigurationResponse)
                .withRequestContext(requestContext)
                .build();
    }

    private PaymentResponse step3(PaymentRequest request) {
        // get data from step2
        String partnerTransactionId = request.getRequestContext().getRequestData().get(CONTEXT_DATA_ORDER);
        String email = request.getRequestContext().getRequestData().get(CONTEXT_DATA_EMAIL);

        if (partnerTransactionId == null || email == null){
            String errorMessage = "Empty step3 data";
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginUtils.truncate(errorMessage, 50))
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        }

        // do the call
        RequestConfiguration requestConfiguration = new RequestConfiguration(request.getContractConfiguration(), request.getEnvironment(), request.getPartnerConfiguration());
        SharegroopAPICallResponse response = sharegroopHttpClient.verifyOrder(requestConfiguration, partnerTransactionId);

        // check the response and the status response
        if (!response.getSuccess()) {
            // return a failure
            LOGGER.error(response.getErrors().get(0));
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(PluginUtils.truncate(response.getErrors().get(0), 50))
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        }
        String status = response.getData().getStatus();
        if (!"confirmed".equalsIgnoreCase(status)) {
            // wrong payment status, return a failure
            String errorMessage = "Wrong transaction status: " + status;
            LOGGER.error(errorMessage);
            return PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(PluginUtils.truncate(errorMessage, 50))
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        }

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

    private PaymentFormConfigurationResponse createForm(String script) {
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
                    .withDescription(PaymentServiceImpl.STEP1_DESCRIPTION)
                    .withScriptImport(scriptImport)
                    .withLoadingScriptAfterImport(script)
                    .withContainer(container)
                    .withOnPay(onPay)
                    .withPerformsAutomaticRedirection(true)
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