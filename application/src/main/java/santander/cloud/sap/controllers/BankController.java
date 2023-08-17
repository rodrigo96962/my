package santander.cloud.sap.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceError;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

import com.sap.cloud.sdk.s4hana.datamodel.odata.services.BankDetailService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBankDetailService;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sap.cloud.sdk.s4hana.connectivity.ErpHttpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.bankdetail.BankDetail;

@RestController
@RequestMapping("/banks")
@PreAuthorize("hasAuthority('User')")
public class BankController{

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(BankController.class);
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('User')")
    public ResponseEntity<String> getBank()
    {
        try {
            
            ErpHttpDestination destination = DestinationAccessor.getDestination("destination-saphub").asHttp().decorate(DefaultErpHttpDestination::new);

            final BankDetailService service = new DefaultBankDetailService();
            final List<BankDetail> result = service.getAllBankDetail().select(BankDetail.ALL_FIELDS).top(5).executeRequest(destination);

            logger.info(String.format("Found %d bank(s).", result.size()));

            return ResponseEntity.ok(new Gson().toJson(result));
       
        } catch( ODataServiceErrorException e ) {

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