package santander.cloud.sap.externalLib;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;
import static santander.cloud.sap.utils.SantanderApiUtils.convertJsonBody;

public class SantanderOpenAPIConfigTest {

    @InjectMocks
    SantanderOpenAPIConfig santanderOpenAPIConfig;

    @Before
    public void init(){

        openMocks(this);
    }

    @Test
    public void convertJsonBodyTest() {
        Map<String, String> workspaceBody = new HashMap<>();
        workspaceBody.put("type", "\"PAYMENTS\"");
        workspaceBody.put("description", "\"Teste BTP\"");
        workspaceBody.put("pixPaymentsActive", "true");
        workspaceBody.put("barCodePaymentsActive", "true");
        workspaceBody.put("bankSlipPaymentsActive", "true");
        workspaceBody.put("bankSlipAvailableActive", "true");
        workspaceBody.put("mainDebitAccount", "{ \"branch\": \"0001\", \"number\": \"000130375431\" }");
        workspaceBody.put("additionalDebitAccounts", "{ \"branch\": \"0427\", \"number\": \"000130005107\" }");

        String workspaceBodyString = convertJsonBody(workspaceBody);

        assertEquals("{ \"barCodePaymentsActive\": true, \"pixPaymentsActive\": true, \"description\": \"Teste BTP\", \"bankSlipPaymentsActive\": true, \"bankSlipAvailableActive\": true, \"mainDebitAccount\": { \"branch\": \"0001\", \"number\": \"000130375431\" }, \"type\": \"PAYMENTS\", \"additionalDebitAccounts\": { \"branch\": \"0427\", \"number\": \"000130005107\" } }",
                    workspaceBodyString);
    }

//    @Test
//    public void getDdaGetTest(){
//        Map<String, String> queryParam = new HashMap<>();
//
//        queryParam.put("originAggregates", String.join(",", "teste"));
//        queryParam.put("originAuthorized", String.join(",", "teste"));
//        queryParam.put("beneficiaryDocument", String.join(",", "teste"));
//        queryParam.put("titleSituation", TitleSituationEnum.ALL.name());
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String initialDateString = simpleDateFormat.format(new Date());
//        queryParam.put("initialDueDate", initialDateString);
//
//        String finalDateString = simpleDateFormat.format(new Date());
//        queryParam.put("finalDueDate", finalDateString);
//
//        queryParam.put("_limit","1");
//        queryParam.put("_offset", "1");
//
//        String result = santanderOpenAPIConfig.getDdaGet("workspace_id", queryParam);
//
//    }
}