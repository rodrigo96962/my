package santander.cloud.sap.service;

import santander.cloud.sap.models.Transaction;

public interface TransactionService {
    void saveEntity(Transaction transaction);
}
