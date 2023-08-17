package santander.cloud.sap.models.json;

import lombok.Data;
import santander.cloud.sap.enums.Type;

@Data
public class BillItem {
    private Type type;
    private Subscription subscription;
    private Product product;
    private RatePlan ratePlan;
    private NetAmount netAmount;
    private ShipTo shipTo;
}
