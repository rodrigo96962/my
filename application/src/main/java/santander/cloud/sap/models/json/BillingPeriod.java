package santander.cloud.sap.models.json;

import lombok.Data;

@Data
public class BillingPeriod {
    private Period period;
    private BillingSettings billingSettings;
}
