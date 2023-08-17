package santander.cloud.sap.service;

import santander.cloud.sap.models.DebitAccount;

import java.util.List;

public interface DebitAccountService {
    void setEntityId(DebitAccount debitAccount);

    void setEntityIdList(List<DebitAccount> debitAccountList);

    Long getEntityId(Long branch, Long numberAccount);
}
