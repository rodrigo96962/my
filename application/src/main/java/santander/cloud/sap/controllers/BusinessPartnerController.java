package santander.cloud.sap.controllers;

import java.time.Duration;
import java.util.List;

import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationLoader;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheExpirationStrategy;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceError;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.datamodel.odata.helper.Order;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.BusinessPartner;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businesspartners")
@PreAuthorize("hasAuthority('User')")
public class BusinessPartnerController {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(BusinessPartnerController.class);

    private static final String CATEGORY_PERSON = "1";

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getBusinessPartners(){

        ScpCfDestinationLoader.Cache.setExpiration(Duration.ofMillis(1), CacheExpirationStrategy.WHEN_CREATED);

        HttpDestination destination = DestinationAccessor.getDestination("destination-saphub").asHttp();

        logger.info(String.format("Tenant ID " + TenantAccessor.getCurrentTenant().getTenantId()));
        try {

            final List<BusinessPartner> businessPartners =
            new DefaultBusinessPartnerService()
                    .getAllBusinessPartner()
                    .select(BusinessPartner.BUSINESS_PARTNER,
                            BusinessPartner.LAST_NAME,
                            BusinessPartner.FIRST_NAME,
                            BusinessPartner.IS_MALE,
                            BusinessPartner.IS_FEMALE,
                            BusinessPartner.CREATION_DATE
                        )
                    .filter(BusinessPartner.BUSINESS_PARTNER_CATEGORY.eq(CATEGORY_PERSON))
                    .orderBy(BusinessPartner.LAST_NAME, Order.ASC)
                    .top(10)
                    .executeRequest(destination);
            
           logger.info(String.format("Found %d business partner(s).", businessPartners.size()));

           return ResponseEntity.ok(new Gson().toJson(businessPartners));
       
        } catch( ODataServiceErrorException e ) {
            // handle the specific error message from the response payload
            ODataServiceError odataError = e.getOdataError();

            logger.error("The OData service responded with an error: {}", odataError);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("oDATA error " + odataError.getODataMessage() +  
                        " URL: " + e.getRequest().getRelativeUri().toString() + " RequestQuery " 
                        + e.getRequest().getRequestQuery()  + " ServicePath "  
                        + e.getRequest().getServicePath());
        
        } catch (final ODataException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage() + " " + e.getRequest());
        }
                
    }
}