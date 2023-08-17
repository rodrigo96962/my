package santander.cloud.sap.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import santander.cloud.sap.models.PartialPayment;
import santander.cloud.sap.repositories.PartialPaymentRepository;
import santander.cloud.sap.service.PartialPaymentService;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartialPaymentServiceImpl implements PartialPaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PartialPaymentServiceImpl.class);

    private final PartialPaymentRepository partialPaymentRepository;

    @Override
    public void saveEntity(PartialPayment partialPayment) {
        partialPaymentRepository.save(partialPayment);
    }
}
