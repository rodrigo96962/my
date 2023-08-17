package santander.cloud.sap.service;

import santander.cloud.sap.models.Bill;
import santander.cloud.sap.models.PixReceipt;

import java.util.List;

public interface BillsService {
    List<Bill> getAll();
    Bill getById(String id);
    List<Bill> getOpen();
    List<Bill> getClosed();
    void savePixReceiptByBillId(PixReceipt pixReceipt, String billId);
}
