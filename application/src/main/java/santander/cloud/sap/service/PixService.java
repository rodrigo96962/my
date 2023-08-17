package santander.cloud.sap.service;

import santander.cloud.sap.models.PixReceipt;
import santander.cloud.sap.models.PixRequest;

public interface    PixService {
    PixReceipt getReceiptPix(String pix_payment_id, String workspace_id);
    PixReceipt postPix(PixRequest pixRequest);

}
