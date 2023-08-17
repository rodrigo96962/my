package santander.cloud.sap.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SantanderApiUtils {
    private static final Logger logger = LoggerFactory.getLogger(SantanderApiUtils.class);

    private static final String DOUBLE_QUOTES = "\"";
    private static final String DOUBLE_DOTS = ": ";
    private static String DATE_FORMAT_PATTERN_US = "yyyy-MM-dd";
    private static String DATE_FORMAT_PATTERN_BR = "dd/MM/yyyy";

    public static final String PAYMENT_DEFAULT_STATUS = "AUTHORIZED";

    public static String convertBodyRequestToURLencoded(Map<String, String> params) {
        logger.info("Generating URL encoded body parameters...");
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
            }
        } catch (Exception e){
            logger.error("Error to generate body: " + e.getMessage());
            return null;
        }
        logger.info("Body parameters to set: " + result);
        return result.toString();
    }

    public static String convertQueryParams(Map<String, String> queryParameters){
        AtomicReference<String> queryParam = new AtomicReference<>("?");
        int lastIndex = queryParameters.size();
        AtomicInteger iterator = new AtomicInteger();

        queryParameters.forEach((parameter, value) -> {
            queryParam.set(queryParam.get().concat(parameter).concat("=").concat(value));
            if (iterator.incrementAndGet() != lastIndex)
                queryParam.set(queryParam.get().concat("&"));
        });

        return queryParam.get();
    }

    public static String convertJsonBody(Map<String, String> BodyParameters){
        AtomicReference<String> requestBody = new AtomicReference<>("{ ");
        int lastIndex = BodyParameters.size();
        AtomicInteger iterator = new AtomicInteger();

        BodyParameters.forEach((parameter, value) -> {
            requestBody.set(requestBody.get().concat(DOUBLE_QUOTES).concat(parameter).concat(DOUBLE_QUOTES)
                    .concat(DOUBLE_DOTS).concat(value));
            if (iterator.incrementAndGet() != lastIndex)
                requestBody.set(requestBody.get().concat(", "));
        });

        requestBody.set(requestBody.get().concat(" }"));

        return requestBody.get();
    }

    public static String getParamWithDoubleQuotes(String param){
        return DOUBLE_QUOTES.concat(param).concat(DOUBLE_QUOTES);
    }

    public static HashMap<String, String> getPaymentsPatchDefaultParams(Double paymentValue){
        HashMap<String, String> patchJsonBody = new HashMap<>();
        patchJsonBody.put("paymentValue", paymentValue.toString());
        patchJsonBody.put("status", getParamWithDoubleQuotes(PAYMENT_DEFAULT_STATUS));

        return patchJsonBody;
    }

    public static String convertDateToStringUsFormat(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN_US);

        return simpleDateFormat.format(date);
    }

    public static String convertDateToStringBrFormat(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN_BR);

        return simpleDateFormat.format(date);
    }
}
