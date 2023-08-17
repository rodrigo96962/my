package santander.cloud.sap.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import santander.cloud.sap.enums.BillStatus;
import santander.cloud.sap.enums.BillingType;
import santander.cloud.sap.enums.Currency;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @Id
    private String id;

    @Enumerated
    private BillingType billingType;

    @Enumerated
    private BillStatus billStatus;

    @Enumerated
    private Currency currency;

    @OneToOne
    private Market market;

    @OneToOne
    private Customer customer;

    @OneToOne
    private Payer payer;

    @OneToOne
    private PixReceipt pixReceipt;

    private String dueDate;

    private Boolean createInvoice;

    private Double netAmount;
}
