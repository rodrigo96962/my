package santander.cloud.sap.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import santander.cloud.sap.enums.Country;
import santander.cloud.sap.enums.Type;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Payer {

    @Id
    private String id;
    private Country country;
    private Type type;
    private String documentNumber;
    private String documentType;
    private String name;
}
