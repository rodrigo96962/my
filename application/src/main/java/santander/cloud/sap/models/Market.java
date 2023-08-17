package santander.cloud.sap.models;

import lombok.Data;
import santander.cloud.sap.enums.Currency;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Market {
    @Id
    private String id;
    private String timeZone;
    private Currency currency;
    private String priceType;
}
