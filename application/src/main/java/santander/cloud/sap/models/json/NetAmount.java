package santander.cloud.sap.models.json;

import lombok.Data;
import santander.cloud.sap.enums.Currency;

@Data
public class NetAmount {
    private Currency currency;
    private Double amount;
}
