package santander.cloud.sap.models.json;

import lombok.Data;

@Data
public class Subscription {
    private String id;
    private Integer documentNumber;
    private String itemId;
    private Integer subscriptionDocumentId;
}
