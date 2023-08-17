package santander.cloud.sap.externalLib;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import santander.cloud.sap.enums.PayloadTypeEnum;
import santander.cloud.sap.models.BearerToken;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static santander.cloud.sap.enums.PayloadTypeEnum.JSON_BODY;
import static santander.cloud.sap.enums.PayloadTypeEnum.URL_ENCODED_BODY;
import static santander.cloud.sap.utils.SantanderApiUtils.*;

@Setter
@Getter
@RequiredArgsConstructor
@Component
public class SantanderOpenAPIConfig {

    private static final Logger logger = LoggerFactory.getLogger(SantanderOpenAPIConfig.class);

    @Value("${workspace.id}")
    private String workspaceId;

    @Value("${client.id}")
    private String client_id;

    @Value("${client.secret}")
    private String client_secret;

    @Value("${openapi.url}")
    private String urlOpenApi;

    @Value("${destination.name}")
    private String santanderCertDestinationName;

    @Value("${main.debit.account.number}")
    private String mainDebitAccountNumber;

    @Value("${main.debit.account.branch}")
    private String mainDebitAccountBranch;

    @Value("${additional.debit.account.number}")
    private String additionalDebitAccountNumber;

    @Value("${additional.debit.account.branch}")
    private String additionalDebitAccountBranch;

    @Value("${bank.id}")
    private String defaultBankId;

    @Value("${final.payer.name}")
    private String finalPayerName;

    @Value("${final.payer.document.type}")
    private String finalPayerDocumentType;

    @Value("${final.payer.document.number}")
    private String finalPayerDocumentNumber;

    private final String apiAuthToken = "/auth/oauth/v2/token";
    private final String apiPaymentHub = "/management_payments_partners/v1";
    private final String apiWorkspaces = apiPaymentHub + "/workspaces";
    private final String apiBalance = "/bank_account_information/v1";

    private final String apiPixSufix = "/pix_payments";
    private final String apiDdaSufix = "/available_bank_slips";
    private final String apiPaymentsSufix = "/bank_slip_payments";
    private final String apiBalanceFirstSufix = "/banks/";
    private final String apiBalanceSecondSufix = "/balances/";

    private final String content_type_urlEncoded = "application/x-www-form-urlencoded";
    private final String content_type_json = "application/json";
    private final String grant_type = "client_credentials";

    private final String param_name_client_id = "client_id";
    private final String param_name_client_secret = "client_secret";
    private final String param_name_content_type = "Content-Type";
    private final String param_name_grant_type = "grant_type";
    private final String param_name_apiKey = "X-Application-key"; // value of this header is same as client_id;

    private final String separator = "/";

    private BearerToken bearerToken = new BearerToken();

    private LocalDateTime lastTokenCall = LocalDateTime.now();

    private HttpClient httpClient;

    public HttpClient getHttpClient() {

        if (isNull(this.httpClient)){
            logger.info("Getting cert destination...");
            HttpDestination httpDestination = DestinationAccessor
                    .getDestination(santanderCertDestinationName)
                    .asHttp();

            this.httpClient = HttpClientAccessor.getHttpClient(httpDestination);
        } else {
            logger.info("Already exists HTTPClient instance.");
        }

        return httpClient;
    }

    public String getApiAuthToken() {
        return this.urlOpenApi + apiAuthToken;
    }

    public String getApiWorkspaces() {
        return this.urlOpenApi + apiWorkspaces;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getApiPixGetAndPatch(String workspace_id, String pix_payment_id) {
        return getApiWorkspaces() + separator + workspace_id + this.apiPixSufix + separator + pix_payment_id;
    }

    public String getApiPixPost(String workspace_id) {
        return getApiWorkspaces() + separator + workspace_id + this.apiPixSufix;
    }

    public String getApiPaymentPost(String workspace_id) {
        return getApiWorkspaces() + separator + workspace_id + this.apiPaymentsSufix;
    }

    public String getApiPaymentGetAndPatch(String workspace_id, String payment_id) {
        return getApiWorkspaces() + separator + workspace_id + this.apiPaymentsSufix + separator + payment_id;
    }

    public String getWorkspacesGet(String workspace_id) {
        if(isNull(workspace_id))
            return getApiWorkspaces();

        return getApiWorkspaces() + separator + workspace_id;
    }

    public String getDdaGet(String workspace_id){
        String url = getApiWorkspaces().concat(separator)
                .concat(workspace_id)
                .concat(this.apiDdaSufix);

        return url;
    }

    public String executeHttpRequest(Map<PayloadTypeEnum, HashMap<String, String>> payload, RequestMethod httpType, String url) throws Exception {
        HttpUriRequest httpUriRequest;
        String responseString;

        url = setQueryParams(url, payload.get(PayloadTypeEnum.QUERY_PARAM));

        if(httpType.equals(RequestMethod.GET)) {
            httpUriRequest = new HttpGet(url);
        } else if (httpType.equals(RequestMethod.POST)) {
            HttpPost postCall = new HttpPost(url);
            setBody(postCall, payload);
            httpUriRequest = postCall;
        } else if (httpType.equals(RequestMethod.PATCH)) {
            HttpPatch patchCall = new HttpPatch(url);
            setBody(patchCall, payload);
            httpUriRequest = patchCall;
        } else {
            throw new Exception("Http Type Unsupported");
        }

        setHeaders(httpUriRequest, payload.get(PayloadTypeEnum.HEADER));

        HttpResponse response = getHttpClient().execute(httpUriRequest);

        int responseCode = response.getStatusLine().getStatusCode();

        responseString = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);

        if (responseCode != HttpStatus.SC_OK && responseCode != HttpStatus.SC_CREATED) {
            throw new Exception(String.format("Error to call method: {%s} ; URL: {%s} ; HttpStatusResponse: {%d} ; Response: {%s}",
                    httpType,
                    url,
                    responseCode,
                    responseString));
        }

        return responseString;
    }

    private String setQueryParams(String url, Map<String, String> pathParamMap) {
        logger.info("setQueryParams method");
        if (!isNull(pathParamMap) && !pathParamMap.isEmpty()) {
            logger.info("QueryParams foud!");
            return url.concat(convertQueryParams(pathParamMap));
        }
        logger.info("QueryParams not found!");
        return url;
    }

    private void setHeaders(HttpUriRequest httpRequest, Map<String, String> customHeaders) throws Exception {
        logger.info("setHeaders method, getting Token to set header");
        String bearerToken = getToken();
        //String bearerToken = callJWTsantanderToken();
        httpRequest.setHeader(getParam_name_apiKey(), getClient_id());
        httpRequest.setHeader(HttpHeaders.AUTHORIZATION,
                "Bearer " + bearerToken);

        if (!isNull(customHeaders) && !customHeaders.isEmpty()){
            logger.info("setting custom headers");
            customHeaders.forEach( (header, value) -> {
                httpRequest.setHeader(header, value);
            });
        } else {
            logger.info("no custom headers found");
        }
    }

    private String getToken() throws Exception {

        Long diffence = ChronoUnit.MILLIS.between(lastTokenCall, LocalDateTime.now());

        if (isNull(bearerToken.getAccess_token()) || diffence > bearerToken.getExpires_in()){
            logger.info("current token not valid or expired, generating new one...");
            return callJWTsantanderToken();
        }
        logger.info("current token is valid: {}", bearerToken.getAccess_token());
        return bearerToken.getAccess_token();
    }

    private String callJWTsantanderToken() throws Exception {
        this.lastTokenCall = LocalDateTime.now();
        logger.info("Getting AuthToken... URL: " + getApiAuthToken());
        HttpPost httpPostToGetToken = new HttpPost(getApiAuthToken());
        httpPostToGetToken.setHeader(getParam_name_client_id(), getClient_id());
        httpPostToGetToken.setHeader(getParam_name_client_secret(), getClient_secret());
        httpPostToGetToken.setHeader(getParam_name_content_type(), getContent_type_urlEncoded());

        HashMap<String,String> params = new HashMap();
        params.put(getParam_name_client_id(), getClient_id());
        params.put(getParam_name_client_secret(), getClient_secret());
        params.put(getParam_name_grant_type(), getGrant_type());

        String bodyRequestParams  = convertBodyRequestToURLencoded(params);
        //BearerToken bearerToken;
        try {
            HttpClient httpClient = getHttpClient();

            StringEntity bodyRequest = new StringEntity(bodyRequestParams);

            httpPostToGetToken.setEntity(bodyRequest);

            logger.info("Calling post login to get token");
            HttpResponse response = httpClient.execute(httpPostToGetToken);

            int responseCode = response.getStatusLine().getStatusCode();

            String result = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);

            if(responseCode == HttpStatus.SC_OK)
                bearerToken = new ObjectMapper().readValue(result, BearerToken.class);
            else
                throw new Exception(String.format("Error token call, httpStatusResponse: {%i} ; Response error: {%s}", responseCode, result));

            logger.info("Token correctly found!");
        } catch (Exception e){
            logger.error("Error getting token: " + e.getMessage());
            throw e;
        }
        return bearerToken.getAccess_token();

    }

    private void setBody(HttpEntityEnclosingRequestBase httpRequest, Map<PayloadTypeEnum, HashMap<String, String>> bodyMap) throws UnsupportedEncodingException {
        Map requestBodyJson = bodyMap.get(JSON_BODY);
        Map requestBodyUrlEncoded = bodyMap.get(URL_ENCODED_BODY);

        logger.info("setBody method!");

        if (!isNull(requestBodyJson) && !requestBodyJson.isEmpty()) {
            String bodyJsonString = convertJsonBody(requestBodyJson);
            logger.info("Json body: {}", bodyJsonString);
            StringEntity requestBody = new StringEntity(bodyJsonString);
            httpRequest.setEntity(requestBody);
        } else if (!isNull(requestBodyUrlEncoded) && !requestBodyUrlEncoded.isEmpty()) {
            String bodyUrlEncodedString = convertBodyRequestToURLencoded(requestBodyJson);
            logger.info("UrlEncoded body: {}", bodyUrlEncodedString);
            StringEntity requestBody = new StringEntity(bodyUrlEncodedString);
            httpRequest.setEntity(requestBody);
        } else {
            logger.info("no Body found!");
        }
    }

    public String getBalanceUrlGet(String bankId, String balanceId) {
        return urlOpenApi
                .concat(apiBalance)
                .concat(apiBalanceFirstSufix)
                .concat(bankId)
                .concat(apiBalanceSecondSufix)
                .concat(balanceId);
    }
}
