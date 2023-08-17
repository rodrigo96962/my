package santander.cloud.sap.controllers;

import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import santander.cloud.sap.models.HelloTenantResponse;

@RestController
@RequestMapping( "/hellotenant" )
@PreAuthorize("hasAuthority('Admin')")
public class HelloTenantController
{
    private static final Logger logger = LoggerFactory.getLogger(HelloTenantController.class);

   
    @RequestMapping( method = RequestMethod.GET )
    public ResponseEntity<HelloTenantResponse> getHelloTenant()
    {
        logger.info("I am running!");
        
        logger.info(RequestHeaderAccessor.getHeaderContainer().getHeaderNames().toString());
        
        String name = RequestHeaderAccessor.getHeaderContainer().getHeaderValues("host").toString();

        String id = TenantAccessor.getCurrentTenant().getTenantId();

        String subdomain = RequestHeaderAccessor.getHeaderContainer().getHeaderValues("host").toString(); 

        String userTenant = RequestHeaderAccessor.getHeaderContainer().getHeaderValues("x-proxyuser-ip").toString();;

        return ResponseEntity.ok(new HelloTenantResponse(name, id, subdomain, userTenant));
    }
    	
}
