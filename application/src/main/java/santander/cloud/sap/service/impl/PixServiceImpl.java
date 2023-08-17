package santander.cloud.sap.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import santander.cloud.sap.enums.PayloadTypeEnum;
import santander.cloud.sap.externalLib.SantanderOpenAPIConfig;
import santander.cloud.sap.models.*;
import santander.cloud.sap.repositories.*;
import santander.cloud.sap.service.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

import static santander.cloud.sap.enums.PayloadTypeEnum.JSON_BODY;
import static santander.cloud.sap.utils.SantanderApiUtils.getParamWithDoubleQuotes;
import static santander.cloud.sap.utils.SantanderApiUtils.getPaymentsPatchDefaultParams;

@Service
@RequiredArgsConstructor
@Transactional
public class PixServiceImpl implements PixService {

    private static final Logger logger = LoggerFactory.getLogger(PixServiceImpl.class);

    private final PixReceiptRepository pixReceiptRepository;

    private final SantanderOpenAPIConfig santanderOpenAPIConfig;
    private final AccountService accountService;
    private final BillsService billsService;
    private final PayerService payerService;
    private final TransactionService transactionService;
    private final BeneficiaryService beneficiaryService;
    private final DebitAccountService debitAccountService;
    private final WorkspaceService workspaceService;

    @Override
    public PixReceipt getReceiptPix(String pix_payment_id, String workspace_id) {
        PixReceipt pixReceipt = new PixReceipt();

        try {
            Map<PayloadTypeEnum, HashMap<String, String>> payloadMap = new HashMap<>();
            String url = santanderOpenAPIConfig.getApiPixGetAndPatch(workspace_id, pix_payment_id);

            logger.info("Calling get pix api...");
            String result = santanderOpenAPIConfig.executeHttpRequest(payloadMap, RequestMethod.GET, url);
            pixReceipt = new Gson().fromJson(result, PixReceipt.class);

        } catch (Exception e) {
            logger.error("Error getting pix: " + e.getMessage());
        }

        return pixReceipt;
    }

    @Override
    public PixReceipt postPix(PixRequest pixRequest) {
        PixReceipt pixReceipt = new PixReceipt();

        logger.info("Service starting");

        pixRequest.setWorkspaceId(workspaceService.getValidWorkspaceId(pixRequest.getWorkspaceId()));

        try {
            // Post Pix
            Map<PayloadTypeEnum, HashMap<String, String>> postPayloadMap = new HashMap<>();
            String urlPost = santanderOpenAPIConfig.getApiPixPost(pixRequest.getWorkspaceId());

            postPayloadMap.put(JSON_BODY, getPostJsonBody(pixRequest));

            logger.info("Calling post pix api...");
            String resultPost = santanderOpenAPIConfig.executeHttpRequest(postPayloadMap, RequestMethod.POST, urlPost);
            logger.info("Pix POST Done!");

            pixReceipt = new Gson().fromJson(resultPost, PixReceipt.class);

            if(!accountService.hasBalance(pixRequest.getPaymentValue())){
                logger.error("Insufficient balance! Payment value: {} | Account Balance {}",
                        pixRequest.getPaymentValue(),
                        accountService.getAccountBalance(pixReceipt.getDebitAccount().getBranch().toString(), pixReceipt.getDebitAccount().getNumber().toString(), null));

                return pixReceipt;
            }

            // Patch Pix
            Map<PayloadTypeEnum, HashMap<String, String>> patchPayloadMap = new HashMap<>();
            String urlPatch = santanderOpenAPIConfig.getApiPixGetAndPatch(pixRequest.getWorkspaceId(), pixReceipt.getId());

            patchPayloadMap.put(JSON_BODY, getPaymentsPatchDefaultParams(pixRequest.getPaymentValue()));
            logger.info("Calling patch pix api...");
            String resultPatch = santanderOpenAPIConfig.executeHttpRequest(patchPayloadMap, RequestMethod.PATCH, urlPatch);
            logger.info("Pix PATCH Done!");

            pixReceipt = new Gson().fromJson(resultPatch, PixReceipt.class);

            // pixReceipt PATCH persistence
            persistPixReceipt(pixReceipt, pixRequest.getBillId());

        } catch (Exception e) {
            logger.error("Error pix: " + e.getMessage());
        }

        return pixReceipt;
    }

    private void persistPixReceipt(PixReceipt pixReceipt, String billId) {
        logger.info("pixReceipt PATCH persisting started");

        accountService.debitAccountBalance(pixReceipt.getPaymentValue());

        beneficiaryService.setEntityId(pixReceipt.getBeneficiary());

        debitAccountService.setEntityId(pixReceipt.getDebitAccount());

        pixReceipt.setPayer(payerService.getRandomPayer());

        transactionService.saveEntity(pixReceipt.getTransaction());

        pixReceiptRepository.saveAndFlush(pixReceipt);

        billsService.savePixReceiptByBillId(pixReceipt, billId);

        logger.info("pixReceipt PATCH persisting Done!");
    }

    private HashMap<String, String> getPostJsonBody(PixRequest pixRequest) {
        HashMap<String, String> postJsonBody = new HashMap<>();
        postJsonBody.put("paymentValue", pixRequest.getPaymentValue().toString());
        postJsonBody.put("remittanceInformation", getParamWithDoubleQuotes(pixRequest.getRemittanceInformation()));
        postJsonBody.put("dictCode", getParamWithDoubleQuotes(pixRequest.getDictCode()));
        postJsonBody.put("dictCodeType", getParamWithDoubleQuotes(pixRequest.getDictCodeType()));

        return postJsonBody;
    }
}
