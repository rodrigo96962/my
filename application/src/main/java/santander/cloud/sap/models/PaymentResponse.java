package santander.cloud.sap.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String id;
    private String workspaceId;
    private String code;
    private DebitAccount debitAccount;
    private String status;
    private String rejectReason;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dueDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date accountingDate;
    private Double nominalValue;
    private Double deductedValue;
    private Double addedValue;
    private Double totalValue;
    private Payer payer;
    private Beneficiary beneficiary;
    private Payer finalPayer;
    private PartialPayment partialPayment;
    private Transaction transaction;
    private Double paymentValue;
    private List<String> tags;
}
