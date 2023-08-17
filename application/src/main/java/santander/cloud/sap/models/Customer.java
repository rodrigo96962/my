package santander.cloud.sap.models;


import lombok.Data;
import santander.cloud.sap.enums.Country;
import santander.cloud.sap.enums.Type;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Customer {
    @Id
    private String id;
    private String name;
    private Type type;
    private Country country;
}
