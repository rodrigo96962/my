package santander.cloud.sap.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import santander.cloud.sap.converter.ObjectArrayToDebtAccountListConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;
    private String id;
    private String type;
    private String status;
    private String rejectReason;
    private Date creationDate;
    @ManyToOne
    private DebitAccount mainDebitAccount;
    @ManyToMany
    @JsonDeserialize(converter = ObjectArrayToDebtAccountListConverter.class)
    private List<DebitAccount> additionalDebitAccounts;
    @ElementCollection
    private List<String> tags;
    private String description;
    private String webhookURL;
    private boolean pixPaymentsActive;
    private boolean bankSlipPaymentsActive;
    private boolean barCodePaymentsActive;
    private boolean taxesByFieldPaymentsActive;
    private boolean vehicleTaxesPaymentsActive;
    private boolean bankSlipAvailableActive;
    private boolean bankSlipAvailableWebhookActive;
}
