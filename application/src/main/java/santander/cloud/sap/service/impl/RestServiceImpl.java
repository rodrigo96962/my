package santander.cloud.sap.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import santander.cloud.sap.enums.BillStatus;
import santander.cloud.sap.models.Bill;
import santander.cloud.sap.models.json.BillJson;
import santander.cloud.sap.repositories.*;
import santander.cloud.sap.utils.JsonMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class RestServiceImpl implements CommandLineRunner {

    private final RestTemplate restTemplate;
    private final JsonMapper jsonMapper;
    private final BillRepository billRepository;
    private final PixReceiptRepository pixReceiptRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final TransactionRepository transactionRepository;
    private final DebitAccountRepository debitAccountRepository;
    private final PayerRepository payerRepository;



    @Value("${sap.apikey}")
    private String apiKey;

    @Value("${sap.url}")
    private String url;

    public void initiateDatabase() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("APIKey", apiKey);
        HttpEntity requestEntity = new HttpEntity<Void>(headers);
        ResponseEntity<List<BillJson>> listResponse = null;
        try {
            listResponse = restTemplate
                    .exchange(
                            url,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<List<BillJson>>() {
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(listResponse.getBody())
                .stream().map(JsonMapper::toModel)
                .forEach(this::persistData);
    }

    void persistData(Bill bill) {
        if(bill.getBillStatus() == BillStatus.CLOSED) {
            bill.setNetAmount(bill.getPixReceipt().getTotalValue());
            payerRepository.save(bill.getPayer());
            debitAccountRepository.save(bill.getPixReceipt().getDebitAccount());
            beneficiaryRepository.save(bill.getPixReceipt().getBeneficiary());
            transactionRepository.save(bill.getPixReceipt().getTransaction());
            pixReceiptRepository.save(bill.getPixReceipt());
        }
        billRepository.save(bill);
    }

    @Override
    public void run(String... args) throws Exception {
        initiateDatabase();
    }
}