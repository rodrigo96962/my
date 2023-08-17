package santander.cloud.sap.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import santander.cloud.sap.models.Beneficiary;
import santander.cloud.sap.repositories.BeneficiaryRepository;
import santander.cloud.sap.service.BeneficiaryService;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private static final Logger logger = LoggerFactory.getLogger(BeneficiaryServiceImpl.class);

    private final BeneficiaryRepository beneficiaryRepository;

    @Override
    public void setEntityId(Beneficiary beneficiary) {
        Optional<Beneficiary> beneficiaryOptional = beneficiaryRepository
                .findByDocumentNumberAndDocumentType(beneficiary.getDocumentNumber(), beneficiary.getDocumentType());

        if (beneficiaryOptional.isPresent()){
            beneficiary.setId(beneficiaryOptional.get().getId());
        } else {
            beneficiaryRepository.save(beneficiary);
        }
    }
}
