package santander.cloud.sap.models.json;

import lombok.Data;
import santander.cloud.sap.enums.BillStatus;
import santander.cloud.sap.enums.BillingType;
import santander.cloud.sap.enums.TransferStatus;
import santander.cloud.sap.models.Customer;
import santander.cloud.sap.models.Market;
import santander.cloud.sap.models.Payer;

import java.util.List;

@Data
public class BillJson {
    private String id;
    private Integer documentNumber;
    private MetaData metaData;
    private BillingType billingType;
    private BillStatus billStatus;
    private Closing closing;
    private TransferStatus transferStatus;
    private String DueDate;
    private String BillingDate;
    private String BillingProfileId;
    private BillingPeriod billingPeriod;
    private boolean createInvoice;
    private Market market;
    private String timeZone;
    private Customer customer;
    private Payer payer;
    private BillTo billTo;
    private Payment payment;
    private NetAmount netAmount;
    private List<BillItem> billItems;
    private boolean containsDeletableBillItems;
}
