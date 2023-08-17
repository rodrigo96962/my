package santander.cloud.sap.service;

import santander.cloud.sap.enums.TitleSituationEnum;
import santander.cloud.sap.models.*;

import java.util.Date;
import java.util.List;

public interface PaymentService {
    PaymentDdaResponse getDda(List<String> originAggregates, List<String> originAuthorized, List<String> beneficiaryDocument, TitleSituationEnum titleSituationEnum, Date initialDate, Date finalDate, Integer pageNumber, Integer pageLimit, String workspaceId);

    PaymentReceipt postPayment(PaymentRequest paymentRequest);

    PaymentReceipt getPayment(String barCode);

    List<PaymentReceipt> postBatchPayment(List<PaymentRequest> paymentRequestList, int sleepTime) throws InterruptedException;

    String getDestinationData(String destinationName);
}
