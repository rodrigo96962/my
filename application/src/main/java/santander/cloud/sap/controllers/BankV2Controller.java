package santander.cloud.sap.controllers;

import com.google.gson.Gson;
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

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceError;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

import com.sap.cloud.sdk.s4hana.datamodel.odatav4.services.BankV2Service;
import com.sap.cloud.sdk.s4hana.datamodel.odatav4.services.DefaultBankV2Service;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sap.cloud.sdk.s4hana.connectivity.ErpHttpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odatav4.namespaces.bankv2.Bank;

@RestController
@RequestMapping("/banksV2")
@PreAuthorize("hasAuthority('User')")
public class BankV2Controller {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(BankV2Controller.class);

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('User')")
    public ResponseEntity<String> getBanks() 
    {
        try {

            ErpHttpDestination destination = DestinationAccessor.getDestination("destination-saphub").asHttp().decorate(DefaultErpHttpDestination::new);
           
            final BankV2Service service = new DefaultBankV2Service();
            final List<Bank> result = service.getAllBank().select(Bank.ALL_FIELDS).top(5).execute(destination);

            logger.info(String.format("Found %d Banks v2.", result.size()));

            return ResponseEntity.ok(new Gson().toJson(result));
       
        } catch( ODataServiceErrorException e ) {

            ODataServiceError odataError = e.getOdataError();

            logger.error("The OData service responded with an error: {}", odataError);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body( "oDATA error " + odataError.getODataMessage() +  
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