package santander.cloud.sap.service;

import santander.cloud.sap.models.Payer;

public interface PayerService {
    Payer getRandomPayer();
    void setEntityId(Payer payer);
}
