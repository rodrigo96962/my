package santander.cloud.sap.service;

import santander.cloud.sap.models.Account;

public interface AccountService {

    Account getAccountBalance(String branch, String numberAccount, String bankId);
    Account debitAccountBalance(Double value);
    Boolean hasBalance(Double value);
}
