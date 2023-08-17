package santander.cloud.sap.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceipt {
    @Id
    private String id;
    private String workspaceId;
    private String code;
    private String status;
    private String rejectReason;
    private Double nominalValue;
    private Double deductedValue;
    private Double addedValue;
    private Double totalValue;
    private Double paymentValue;
    private String messageError;
    @ElementCollection
    private List<String> tags;
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @Temporal(TemporalType.DATE)
    private Date accountingDate;
    @ManyToOne(cascade=CascadeType.PERSIST)
    private DebitAccount debitAccount;
    @ManyToOne(cascade=CascadeType.PERSIST)
    private Payer payer;
    @ManyToOne(cascade=CascadeType.PERSIST)
    private Beneficiary beneficiary;
    @ManyToOne(cascade=CascadeType.PERSIST)
    private Payer finalPayer;
    @OneToOne
    private PartialPayment partialPayment;
    @OneToOne
    private Transaction transaction;

    public PaymentReceipt(String messageError){
        this.messageError = messageError;
    }
}
