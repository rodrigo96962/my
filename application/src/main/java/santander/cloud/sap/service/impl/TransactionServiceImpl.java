package santander.cloud.sap.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import santander.cloud.sap.models.Transaction;
import santander.cloud.sap.repositories.TransactionRepository;
import santander.cloud.sap.service.TransactionService;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    @Override
    public void saveEntity(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
