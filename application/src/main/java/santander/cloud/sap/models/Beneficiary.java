package santander.cloud.sap.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankCode;
    private String ispb;
    private Long branch;
    private Long number;
    private String type;
    private String documentNumber;
    private String documentType;
    private String name;
    private String beneficiaryDocument;
    private String beneficiaryType;
    private String beneficiaryName;
    private String finalBeneficiaryDocument;
    private String finalBeneficiaryType;
    private String finalBeneficiaryName;
}
