package santander.cloud.sap.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import santander.cloud.sap.enums.PayloadTypeEnum;
import santander.cloud.sap.enums.TitleSituationEnum;
import santander.cloud.sap.externalLib.SantanderOpenAPIConfig;
import santander.cloud.sap.models.*;
import santander.cloud.sap.repositories.PaymentDdaRepository;
import santander.cloud.sap.repositories.PaymentReceiptRepository;
import santander.cloud.sap.service.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static santander.cloud.sap.enums.PayloadTypeEnum.JSON_BODY;
import static santander.cloud.sap.utils.SantanderApiUtils.*;


@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final SantanderOpenAPIConfig santanderOpenAPIConfig;
    private final WorkspaceService workspaceService;
    private final AccountService accountService;
    private final PayerService payerService;
    private final TransactionService transactionService;
    private final BeneficiaryService beneficiaryService;
    private final DebitAccountService debitAccountService;
    private final PartialPaymentService partialPaymentService;
    private final PaymentReceiptRepository paymentReceiptRepository;
    private final PaymentDdaRepository paymentDdaRepository;

    @Override
    public PaymentDdaResponse getDda(List<String> originAggregates, List<String> originAuthorized, List<String> beneficiaryDocument, TitleSituationEnum titleSituationEnum, Date initialDate, Date finalDate, Integer pageNumber, Integer pageLimit, String workspaceId) {
        if(TitleSituationEnum.PAID.equals(titleSituationEnum))
            return getDdaPaid();

        PaymentDdaResponse paymentDdaResponse = new PaymentDdaResponse();

        try {
            workspaceId = workspaceService.getValidWorkspaceId(workspaceId);

            Map<PayloadTypeEnum, HashMap<String, String>> payloadMap = new HashMap<>();
            String url = santanderOpenAPIConfig.getDdaGet(workspaceId);

            HashMap<String, String> ddaQueryParam = getQueryParamMap(originAggregates, originAuthorized,  beneficiaryDocument, titleSituationEnum, initialDate, finalDate, pageNumber, pageLimit);
            payloadMap.put(PayloadTypeEnum.QUERY_PARAM, ddaQueryParam);

            String result = santanderOpenAPIConfig.executeHttpRequest(payloadMap, RequestMethod.GET, url);

            paymentDdaResponse = new ObjectMapper().readValue(result, PaymentDdaResponse.class);

            persistDda(paymentDdaResponse);

        } catch (Exception e) {
            logger.error("Error getting DDA: " + e.getMessage());
        }

        return paymentDdaResponse;
    }

    private PaymentDdaResponse getDdaPaid() {
        PaymentDdaResponse paymentDdaResponse = new PaymentDdaResponse();

        List<PaymentDda> paymentDdaList = paymentDdaRepository.findByTitleSituationEnum(TitleSituationEnum.PAID);

        if (!isNull(paymentDdaList) && !paymentDdaList.isEmpty()) {
            paymentDdaResponse.setPaymentDdaList(paymentDdaList);
        }

        return paymentDdaResponse;
    }

    public void persistDda(PaymentDdaResponse paymentDdaResponse) {
        LocalDateTime currentDate = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

        for(PaymentDda paymentDda : paymentDdaResponse.getPaymentDdaList()){
            LocalDateTime paymentDate = LocalDateTime.ofInstant(paymentDda.getDueDate().toInstant(),
                    ZoneId.of("America/Sao_Paulo"));

            if(paymentDate.plusDays(1).isBefore(currentDate)){
                paymentDda.setTitleSituationEnum(TitleSituationEnum.OPEN_EXPIRED);
            } else {
                paymentDda.setTitleSituationEnum(TitleSituationEnum.OPEN_TO_EXPIRE);
            }
            beneficiaryService.setEntityId(paymentDda.getBeneficiary());
            beneficiaryService.setEntityId(paymentDda.getFinalBeneficiary());
            paymentDdaRepository.save(paymentDda);
        }
    }

    private HashMap<String, String> getQueryParamMap(List<String> originAggregates, List<String> originAuthorized, List<String> beneficiaryDocument, TitleSituationEnum titleSituationEnum, Date initialDate, Date finalDate, Integer pageNumber, Integer pageLimit) {
        logger.info("Generating DDA get request...");

        HashMap<String, String> queryParam = new HashMap<>();

        if(!isNull(originAggregates) && !originAggregates.isEmpty())
            queryParam.put("originAggregates", String.join(",", originAggregates));

        if(!isNull(originAuthorized) && !originAuthorized.isEmpty())
            queryParam.put("originAuthorized", String.join(",", originAuthorized));

        if(!isNull(beneficiaryDocument) && !beneficiaryDocument.isEmpty())
            queryParam.put("beneficiaryDocument", String.join(",", beneficiaryDocument));

        if(!isNull(titleSituationEnum))
            queryParam.put("titleSituation", titleSituationEnum.name());

        if(!isNull(initialDate)){
            queryParam.put("initialDueDate", convertDateToStringUsFormat(initialDate));
        }

        if(!isNull(finalDate)){
            queryParam.put("finalDueDate", convertDateToStringUsFormat(finalDate));
        }

        if(!isNull(pageNumber) && !isNull(pageLimit)){
            queryParam.put("_limit", pageLimit.toString());
            queryParam.put("_offset", pageNumber.toString());
        }

        return queryParam;
    }

    @Override
    public PaymentReceipt postPayment(PaymentRequest paymentRequest) {
        PaymentReceipt paymentReceipt = new PaymentReceipt();
        String messageError;
        paymentRequest.setWorkspaceId(workspaceService.getValidWorkspaceId(santanderOpenAPIConfig.getWorkspaceId()));

        if(isNull(paymentRequest.getPaymentDate()))
            paymentRequest.setPaymentDate(new Date());

        try {
            // Post Payment
            Map<PayloadTypeEnum, HashMap<String, String>> postPayloadMap = new HashMap<>();
            String urlPost = santanderOpenAPIConfig.getApiPaymentPost(paymentRequest.getWorkspaceId());

            postPayloadMap.put(JSON_BODY, getPostJsonBody(paymentRequest));

            logger.info("Calling post payment api...");
            String resultPost = santanderOpenAPIConfig.executeHttpRequest(postPayloadMap, RequestMethod.POST, urlPost);
            logger.info("payment POST Done!");

            paymentReceipt = new Gson().fromJson(resultPost, PaymentReceipt.class);

            // Validanting balance
            if(!accountService.hasBalance(paymentReceipt.getTotalValue())){
                messageError = String.format("Insufficient balance! Payment value: {%d} | Account Balance {%s}",
                        paymentReceipt.getTotalValue(),
                        accountService.getAccountBalance(paymentReceipt.getDebitAccount().getBranch().toString(),
                                paymentReceipt.getDebitAccount().getNumber().toString(), null));
                logger.error(messageError);

                return new PaymentReceipt(messageError);
            }

            // Pach Pix
            Map<PayloadTypeEnum, HashMap<String, String>> patchPayloadMap = new HashMap<>();
            String urlPatch = santanderOpenAPIConfig.getApiPaymentGetAndPatch(paymentReceipt.getWorkspaceId(), paymentReceipt.getId());

            patchPayloadMap.put(JSON_BODY, getPatchJsonBody(paymentReceipt.getTotalValue(), paymentReceipt.getPayer()));
            logger.info("Calling patch payment api...");
            String resultPatch = santanderOpenAPIConfig.executeHttpRequest(patchPayloadMap, RequestMethod.PATCH, urlPatch);
            logger.info("Payment PATCH Done!");

            paymentReceipt = new Gson().fromJson(resultPatch, PaymentReceipt.class);

            // paymentResponse PATCH persistence
            persistPaymentReceipt(paymentReceipt);

        } catch (Exception e) {
            messageError = String.format("Error trying to call Santander API, message error: {%s}", e.getMessage());
            logger.error(messageError);
            paymentReceipt.setMessageError(messageError);
        }

        return paymentReceipt;
    }

    @Override
    public PaymentReceipt getPayment(String barCode) {
        PaymentReceipt paymentReceipt = new PaymentReceipt();

        Optional<PaymentReceipt> paymentReceiptOptional = paymentReceiptRepository.findByCode(barCode);

        if(!paymentReceiptOptional.isPresent()) {
            logger.info("Not found payment receipt in DB to barCode: {}", barCode);
            return paymentReceipt;
        }

        paymentReceipt = paymentReceiptOptional.get();

        if (!paymentReceipt.getStatus().equals("PAYED")) {
            logger.info("Payment receipt status != PAYED, trying to get status updated from OpenAPI. BarCode = {} ; Current status = {}",
                    barCode, paymentReceipt.getStatus());
            paymentReceipt = getPaymentReceipt(paymentReceipt.getId(), paymentReceipt.getWorkspaceId());

            if(isNull(paymentReceipt.getId()))
                logger.info("Not found payment receipt on OpenAPI to barcode {}", barCode);
            else
                persistPaymentReceipt(paymentReceipt);

        }

        return paymentReceipt;
    }

    @Override
    public List<PaymentReceipt> postBatchPayment(List<PaymentRequest> paymentRequestList, int sleepTime) throws InterruptedException {
        List<PaymentReceipt> paymentReceiptList = new ArrayList<>();
        for(PaymentRequest paymentRequest : paymentRequestList) {
            PaymentReceipt paymentReceipt = postPayment(paymentRequest);
            paymentReceiptList.add(paymentReceipt);
            TimeUnit.SECONDS.sleep(sleepTime);
        }

        return paymentReceiptList;
    }

    @Override
    public String getDestinationData(String destinationName) {
        HttpDestination httpDestination = DestinationAccessor
                .getDestination(destinationName)
                .asHttp();
        BasicCredentials credentialsOption = httpDestination.getBasicCredentials().getOrElse(new BasicCredentials("userError", "passwordError"));

        return "Username = ".concat(credentialsOption.getUsername()).concat("/ Password = ").concat(credentialsOption.getPassword());
    }

    public PaymentReceipt getPaymentReceipt(String paymentId, String workspaceId) {
        PaymentReceipt paymentReceipt = new PaymentReceipt();

        try {
            Map<PayloadTypeEnum, HashMap<String, String>> payloadMap = new HashMap<>();
            String url = santanderOpenAPIConfig.getApiPaymentGetAndPatch(workspaceId, paymentId);

            logger.info("Calling get payment api...");
            String result = santanderOpenAPIConfig.executeHttpRequest(payloadMap, RequestMethod.GET, url);
            paymentReceipt = new Gson().fromJson(result, PaymentReceipt.class);

        } catch (Exception e) {
            logger.error("Error getting payment: " + e.getMessage());
        }

        return paymentReceipt;
    }

    private void persistPaymentReceipt(PaymentReceipt paymentReceipt) {
        logger.info("paymentResponse PATCH persisting started");

        accountService.debitAccountBalance(paymentReceipt.getPaymentValue());

        beneficiaryService.setEntityId(paymentReceipt.getBeneficiary());

        debitAccountService.setEntityId(paymentReceipt.getDebitAccount());

        transactionService.saveEntity(paymentReceipt.getTransaction());

        payerService.setEntityId(paymentReceipt.getPayer());

        payerService.setEntityId(paymentReceipt.getFinalPayer());

        partialPaymentService.saveEntity(paymentReceipt.getPartialPayment());

        paymentReceiptRepository.save(paymentReceipt);

        Optional<PaymentDda> paymentDdaOptional = paymentDdaRepository.findById(paymentReceipt.getCode());
        if (paymentDdaOptional.isPresent()) {
            PaymentDda paymentDda = paymentDdaOptional.get();
            paymentDda.setTitleSituationEnum(TitleSituationEnum.PAID);
            paymentDdaRepository.save(paymentDda);
        }

        logger.info("paymentResponse PATCH persisting Done!");
    }

    private HashMap<String, String> getPostJsonBody(PaymentRequest paymentRequest) {
        HashMap<String, String> postJsonBody = new HashMap<>();

        postJsonBody.put("code", getParamWithDoubleQuotes(paymentRequest.getBarCode()));
        postJsonBody.put("paymentDate", getParamWithDoubleQuotes(convertDateToStringUsFormat(paymentRequest.getPaymentDate())));

        return  postJsonBody;
    }

    private HashMap<String, String> getPatchJsonBody(Double paymentValue, Payer payer) {
        HashMap<String, String> patchJsonBody = getPaymentsPatchDefaultParams(paymentValue);
        patchJsonBody.put("finalPayer", getFinalPayer(payer));

        return patchJsonBody;
    }

    private String getFinalPayer(Payer payer){
        String finalPayer = " { " +
                "        \"name\": \"" + santanderOpenAPIConfig.getFinalPayerName() + "\", " +
                "        \"documentType\": \"" + santanderOpenAPIConfig.getFinalPayerDocumentType() + "\", " +
                "        \"documentNumber\": \"" + santanderOpenAPIConfig.getFinalPayerDocumentNumber() + "\" " +
                "    }";

        return finalPayer;
    }
}
