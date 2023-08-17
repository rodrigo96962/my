package santander.cloud.sap.models;

import lombok.Data;
import santander.cloud.sap.enums.PixCodeType;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class PixCode {
    @Id
    private Integer id;
    private String code;
    private PixCodeType type;
}
