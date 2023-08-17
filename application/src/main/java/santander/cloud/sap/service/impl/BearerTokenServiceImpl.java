package santander.cloud.sap.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import santander.cloud.sap.externalLib.SantanderOpenAPIConfig;
import santander.cloud.sap.models.BearerToken;
import santander.cloud.sap.service.BearerTokenService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static santander.cloud.sap.utils.SantanderApiUtils.convertBodyRequestToURLencoded;

@Service
@RequiredArgsConstructor
public class BearerTokenServiceImpl implements BearerTokenService {
    private static final Logger logger = LoggerFactory.getLogger(BearerTokenServiceImpl.class);

    private final SantanderOpenAPIConfig santanderOpenAPI;

    public String getJWTsantanderToken(){
        logger.info("Getting AuthToken... URL: " + santanderOpenAPI.getApiAuthToken());
        HttpPost httpPostToGetToken = new HttpPost(santanderOpenAPI.getApiAuthToken());
        httpPostToGetToken.setHeader(santanderOpenAPI.getParam_name_client_id(), santanderOpenAPI.getClient_id());
        httpPostToGetToken.setHeader(santanderOpenAPI.getParam_name_client_secret(), santanderOpenAPI.getClient_secret());
        httpPostToGetToken.setHeader(santanderOpenAPI.getParam_name_content_type(), santanderOpenAPI.getContent_type_urlEncoded());

        HashMap<String,String> params = new HashMap();
        params.put(santanderOpenAPI.getParam_name_client_id(), santanderOpenAPI.getClient_id());
        params.put(santanderOpenAPI.getParam_name_client_secret(), santanderOpenAPI.getClient_secret());
        params.put(santanderOpenAPI.getParam_name_grant_type(), santanderOpenAPI.getGrant_type());

        String bodyRequestParams  = convertBodyRequestToURLencoded(params);

        BearerToken bearerToken = new BearerToken();

        try {
            HttpClient httpClient = santanderOpenAPI.getHttpClient();

            StringEntity bodyRequest = new StringEntity(bodyRequestParams);

            httpPostToGetToken.setEntity(bodyRequest);

            HttpResponse response = httpClient.execute(httpPostToGetToken);

            int responseCode = response.getStatusLine().getStatusCode();

            String result = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);

            if(responseCode == HttpStatus.SC_OK)
                bearerToken = new ObjectMapper().readValue(result, BearerToken.class);
            else
                logger.error("Error token call, response error: " + result);

        } catch (Exception e){
            logger.error("Error getting token: " + e.getMessage());
            return null;
        }
        return bearerToken.getAccess_token();
    }

}
