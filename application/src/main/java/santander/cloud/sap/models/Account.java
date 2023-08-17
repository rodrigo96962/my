package santander.cloud.sap.models;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double availableAmount;
    private String availableAmountCurrency;
    private Double blockedAmount;
    private String blockedAmountCurrency;
    private Double automaticallyInvestedAmount;
    private String automaticallyInvestedAmountCurrency;
    private boolean updated;
    @OneToOne
    private DebitAccount debitAccount;
}
