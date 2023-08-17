package santander.cloud.sap.models.json;

import lombok.Data;
import santander.cloud.sap.enums.Country;
import santander.cloud.sap.enums.Type;

@Data
public class BillTo {
    private String id;
    private Country country;
    private Type type;
}