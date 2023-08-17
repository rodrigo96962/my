package santander.cloud.sap.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete ;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sap.cloud.sdk.s4hana.connectivity.ErpHttpDestination;

import santander.cloud.sap.models.App;
import santander.cloud.sap.models.CFData;
import santander.cloud.sap.models.Data;
import santander.cloud.sap.models.Destination;
import santander.cloud.sap.models.Destinations;
import santander.cloud.sap.models.Domain;
import santander.cloud.sap.models.Relationships;
import santander.cloud.sap.models.Route;
import santander.cloud.sap.models.Space;

@Service
public class CfRoutesService
{
    private static final Logger logger = LoggerFactory.getLogger(CfRoutesService.class);
    private final ErpHttpDestination destination = DestinationAccessor.getDestination("firstmtjavaapp-cfapi").asHttp().decorate(DefaultErpHttpDestination::new);
//    private final ErpHttpDestination destination = null;

    public CFData getCFinfo(String appName){
    
        logger.info("I am running!");
        
        HttpEntity entity = null;
        String result ="{}";

        JSONObject vcap = new JSONObject(System.getenv("VCAP_APPLICATION"));
        String orgId = vcap.getString("organization_id");
        String spaceId = vcap.getString("space_id");
        String appUrl = vcap.getJSONArray("application_uris").getString(0);
        String domainUrl = appUrl.substring(appUrl.indexOf(".") + 1);
        String appId = "";
        String domainId = "";

        try {
            
            String baseUri = destination.getUri().toString();
            String url = baseUri + "/v3/apps?organization_guids=" + orgId 
                                + "&space_guids=" + spaceId
                                + "&names=" + appName;
                                
            HttpGet request = new HttpGet(url);
            HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
            HttpResponse response = httpClient.execute(request);

            entity = response.getEntity();

            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);
                logger.info(result);

                JSONObject appDataApp = new JSONObject(result);
                JSONArray resources = appDataApp.getJSONArray("resources");
                JSONObject resource = resources.getJSONObject(0);
                appId = resource.getString("guid");

                
            }

            url = baseUri   + "/v3/domains?names=" + domainUrl;
            
            request = new HttpGet(url);
            response = httpClient.execute(request);

            entity = response.getEntity();

            if (entity != null) {
                // return it as a String
                result = EntityUtils.toString(entity);
                logger.info("CF Domain + " + result);

                JSONObject appDataDomain = new JSONObject(result);
                JSONArray resourcesDomain = appDataDomain.getJSONArray("resources");
                JSONObject resourceDomain = resourcesDomain.getJSONObject(0);
                domainId = resourceDomain.getString("guid");
                
            }

        } catch (JSONException | IOException e) {
                logger.error(e.getMessage());
        } 
        
        return CFData.builder()
                        .domainUrl(domainUrl)
                        .domainId(domainId)
                        .spaceId(spaceId)
                        .appId(appId)
                .build();
        
    }


    public String createRoute(String subscribedSubdomain, String routeAppName){

        
        logger.info("I am createRoute method");

        String baseUri = destination.getUri().toString();
        String url = baseUri + "/v3/routes";
        CFData cfdata = getCFinfo(routeAppName);
        String responseDest = ""; 
        String response = ""; 

        Route route = Route.builder()
                    .host(subscribedSubdomain + "-" + routeAppName) 
                    .relationships(Relationships.builder()
                                    .space(Space.builder()
                                            .data( Data.builder()
                                                    .guid(cfdata.getSpaceId())
                                                    .build()
                                                )
                                            .build())
                                    .domain(Domain.builder()
                                            .data(Data.builder()
                                                    .guid(cfdata.getDomainId())
                                                    .build()
                                                    )
                                            .build()
                                    )
                                .build()      
                    ).build();
        
        try {
            
            String routeEntity = new Gson().toJson(route);

            logger.info("Route Entity " + routeEntity);
            //Criando a Rota
            StringEntity entity = new StringEntity(routeEntity);
        
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // Create a custom response handler
            ResponseHandler <String> responseHandler = responseReult -> {
                int status = responseReult.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity responseEntity = responseReult.getEntity();
                    return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status Domain: " + status);
                }
            };

            HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
            response = httpClient.execute(httpPost, responseHandler);
            JSONObject routeCreatedData = new JSONObject(response);

            //mapeando a rota a aplicacao
            url = baseUri + "/v3/routes/" + routeCreatedData.getString("guid") + "/destinations";
            
            List<Destination> listDestination = new ArrayList<>();
            listDestination.add(Destination.builder()
                                        .app(App.builder()
                                                .guid(cfdata.getAppId())
                                                .build())
                                        .build()
                                        );
                                                
            Destinations destinations = Destinations.builder()
                                            .destinations(listDestination)
                                        .build();

            String destEntity = new Gson().toJson(destinations);

            StringEntity entityDest = new StringEntity(destEntity);
        
            httpPost = new HttpPost(url);
            httpPost.setEntity(entityDest);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // Create a custom response handler
            ResponseHandler <String> responseHandlerDest = responseReult -> {
                int status = responseReult.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity responseEntity = responseReult.getEntity();
                    return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status Dest: " + status);
                }
            };

            logger.info("Destination url " + url);
            logger.info("Destination Entity " + destEntity);
            
            responseDest = httpClient.execute(httpPost, responseHandlerDest);

        } catch (ClientProtocolException e) {
            logger.error(e.getMessage());
        }catch (UnsupportedEncodingException e1) {
                logger.error(e1.getMessage());
            
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return responseDest;

    }

    public String deleteRoute(String subscribedSubdomain, String routeAppName){

        //final ErpHttpDestination destination = DestinationAccessor.getDestination("firstmtjavaapp-cfapi").asHttp().decorate(DefaultErpHttpDestination::new);
        logger.info("I am delete method");

        String baseUri = destination.getUri().toString();
        CFData cfdata = getCFinfo(routeAppName);
        String url = baseUri + "/v3/apps/" + cfdata.getAppId() + "/routes?hosts=" + subscribedSubdomain + "-" + routeAppName;
        String routeId = "";
        String responseDelete = "";
        
        try {
            
            HttpGet request = new HttpGet(url);
            HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
            HttpResponse response = httpClient.execute(request);
            
            logger.info(url);

            HttpEntity entity = response.getEntity();

            if (entity != null) {

                String result = EntityUtils.toString(entity);
                
                logger.info("Delete route Search " + result);

                JSONObject appDataApp = new JSONObject(result);
                JSONArray resources = appDataApp.getJSONArray("resources");
                JSONObject resource = resources.getJSONObject(0);
                routeId = resource.getString("guid");
            
                url = baseUri + "/v3/routes/" + routeId;

                HttpDelete httpDelete = new HttpDelete(url);
                
                // Create a custom response handler
                ResponseHandler <String> responseHandler = responseReult -> {
                    int status = responseReult.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity responseEntity = responseReult.getEntity();
                        return responseEntity != null ? EntityUtils.toString(responseEntity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status Delete Route: " + status);
                    }
                };

                responseDelete = httpClient.execute(httpDelete, responseHandler);

            }else{
                logger.error("Route not found " + subscribedSubdomain);
            }
          

        } catch (ClientProtocolException e) {
            logger.error(e.getMessage());
        }catch (UnsupportedEncodingException e1) {
                logger.error(e1.getMessage());
            
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        
        return responseDelete;

    }
    	
}



