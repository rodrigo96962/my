package santander.cloud.sap.models.json;

import lombok.Data;

import java.util.List;

@Data
public class Charge {
    private String metricId;
    private String ratingType;
    private Period ratingPeriod;
    private Quantity consumedQuantity;
    private Quantity chargedQuantity;
    private Quantity includedQuantity;
    private NetAmount netAmount;
    private boolean withReferenceToPricingScheme;
    private boolean reversal;
    private List<Object> externalObjectReferences;
}
