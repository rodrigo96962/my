package santander.cloud.sap.models.json;

import lombok.Data;
import santander.cloud.sap.enums.Country;
import santander.cloud.sap.enums.Type;

import java.util.List;

@Data
public class ShipTo {
    private String id;
    private Country country;
    private Type type;
    private List<Object> externalObjectReferences;
    private List<Charge> charges;
    private boolean markedForDeletion;
    private List<Object> credits;
}
