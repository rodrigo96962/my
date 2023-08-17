package santander.cloud.sap.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import santander.cloud.sap.models.ApplicationDependency;

@RestController
@RequestMapping( "/callback/v1.0/")
public class SubscriptionHandler
{
    @Autowired
    private CfRoutesService cfroutesservice;

    private static final String APP_ROUTE_NAME = "first-mt-java-app";
    private static final String APP_HOST = "cfapps.us10-001.hana.ondemand.com";
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionHandler.class);

    @PutMapping("tenants/{tenantId}")
    public ResponseEntity<String> onSubscription(@RequestBody String body, @PathVariable String tenantId)
    {
        logger.info("I am subscribing ! "  + body);
        
        logger.info("Sou o tenanty " + tenantId);

        JsonObject data = new Gson().fromJson(body, JsonObject.class);
        
        String tenantSubdomain = data.get("subscribedSubdomain").getAsString();

        String tenantAppURL = "https://" + tenantSubdomain + "-" + APP_ROUTE_NAME + "." + APP_HOST;

        cfroutesservice.createRoute(tenantSubdomain, APP_ROUTE_NAME);
        
        return ResponseEntity.ok(tenantAppURL);
    }

    @DeleteMapping("tenants/{tenantId}")
    public ResponseEntity<String> onUnSubscription(@RequestBody String body, @PathVariable String tenantId)
    {
        logger.info("I am delete ! "  + body);
        
        logger.info("Estou aqui! tenanty " + tenantId);

        JsonObject data = new Gson().fromJson(body, JsonObject.class);
        
        String tenantSubdomain = data.get("subscribedSubdomain").getAsString();

        cfroutesservice.deleteRoute(tenantSubdomain, APP_ROUTE_NAME);

        return ResponseEntity.ok("");
    }

    @GetMapping("dependencies")
    public ResponseEntity<List<ApplicationDependency>> getDependencies(@RequestParam String tenantId)
    {
        logger.info("I am dependencies ! ");
        
       
        List<ApplicationDependency> dependenciesList = new ArrayList<>();
        JSONObject vcap;
        String destinationXsAppName = "";
        try {
            vcap = new JSONObject(System.getenv("VCAP_SERVICES"));
            JSONArray arr = vcap.getJSONArray("destination");
            JSONObject credentials = arr.getJSONObject(0).getJSONObject("credentials");
            destinationXsAppName = credentials.getString("xsappname");
            if(!destinationXsAppName.isEmpty()){
                dependenciesList.add(new ApplicationDependency(destinationXsAppName));
            }
            
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
        return ResponseEntity.ok(dependenciesList) ;
    }
    	
}
