package santander.cloud.sap.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import santander.cloud.sap.converter.DateToStringBrFormatConverter;
import santander.cloud.sap.converter.StringToDoubleConverter;
import santander.cloud.sap.enums.TitleSituationEnum;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDda {
    @Id
    @JsonProperty("code")
    private String barCode;
    @Enumerated(EnumType.STRING)
    private TitleSituationEnum titleSituationEnum;
    @JsonSerialize(converter = DateToStringBrFormatConverter.class)
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @JsonSerialize(converter = DateToStringBrFormatConverter.class)
    @Temporal(TemporalType.DATE)
    private Date paymentLimitDate;
    private String payerDocumentNumber;
    @JsonDeserialize(converter = StringToDoubleConverter.class)
    private Double nominalValue;
    @ManyToOne(cascade=CascadeType.PERSIST)
    private Beneficiary beneficiary;
    @ManyToOne(cascade=CascadeType.PERSIST)
    private Beneficiary finalBeneficiary;
}
