package santander.cloud.sap.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private String workspaceId;
    @NotEmpty
    private String barCode;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date paymentDate;
}
