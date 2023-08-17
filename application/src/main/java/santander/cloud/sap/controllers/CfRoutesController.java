package santander.cloud.sap.controllers;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import santander.cloud.sap.service.impl.CfRoutesService;


@RestController
public class CfRoutesController {

    private static final Logger logger = LoggerFactory.getLogger(CfRoutesController.class);

    @Autowired
    private CfRoutesService cfroutesservice;
    
    @RequestMapping( path = "/cfi", method = RequestMethod.GET, produces = "application/json" )
    public ResponseEntity<String> getCFinfoControllerCreate(){
        
        logger.info("I am creating route!");
        

        String result = "";
        try {
            
            result = cfroutesservice.createRoute("santander-xssqvg46", "first-mt-java-app");

        } catch (JSONException e) {
                logger.error(e.getMessage());
        } 
        return ResponseEntity.ok(result);
        
    }

    @RequestMapping( path = "/cfd", method = RequestMethod.GET, produces = "application/json" )
    public ResponseEntity<String> getCFinfoControllerDelete(){
        
        logger.info("I am delete route!");
        
        String result = "";
        try {
            
            result = cfroutesservice.deleteRoute("santander-xssqvg46", "first-mt-java-app");

        } catch (JSONException e) {
                logger.error(e.getMessage());
        } 
        return ResponseEntity.ok(result);
        
    }

}
