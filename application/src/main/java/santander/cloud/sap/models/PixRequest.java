package santander.cloud.sap.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixRequest {
    @Min(value=1)
    private Double paymentValue;
    @NotEmpty
    private String remittanceInformation;
    @NotEmpty
    private String dictCode;
    @NotEmpty
    private String dictCodeType;
    private String workspaceId;
    @NotEmpty
    private String billId;

}
