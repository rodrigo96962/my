package santander.cloud.sap.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PixReceipt {
    @Id
    private String id;
    private String workspaceId;
    @ManyToOne
    private DebitAccount debitAccount;
    private String status;
    @ElementCollection
    private List<String> tags;
    private String dictCodeType;
    private String dictCode;
    private Double nominalValue;
    private Double totalValue;
    @ManyToOne
    private Payer payer;
    @ManyToOne
    private Beneficiary beneficiary;
    @OneToOne
    private Transaction transaction;
    private Double paymentValue;
}
