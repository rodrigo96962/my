package santander.cloud.sap.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import santander.cloud.sap.models.Payer;
import santander.cloud.sap.repositories.PayerRepository;
import santander.cloud.sap.service.PayerService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class PayerServiceImpl implements PayerService {

    private final PayerRepository payerRepository;

    @Override
    public Payer getRandomPayer() {
        List<Payer> payerList = payerRepository.findAll();
        Random random = new Random();

        if (payerList.isEmpty())
            return null;

        return payerList.get(random.nextInt(payerList.size()));
    }

    @Override
    public void setEntityId(Payer payer) {
        Optional<Payer> payerOptional = payerRepository.findById(payer.getDocumentType().concat(payer.getDocumentNumber()));

        if (payerOptional.isPresent()){
            payer.setId(payerOptional.get().getId());
        } else {
            payer.setId(payer.getDocumentType().concat(payer.getDocumentNumber()));
            payerRepository.save(payer);
        }
    }


}
