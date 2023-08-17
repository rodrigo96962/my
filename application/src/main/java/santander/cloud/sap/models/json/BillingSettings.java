package santander.cloud.sap.models.json;

import lombok.Data;

@Data
public class BillingSettings {
    private Integer billingDateDelay;
    private String adjustmentCycle;
    private String billingOfBackdatedSubscriptions;
}
