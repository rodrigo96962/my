package santander.cloud.sap.models.json;

import lombok.Data;
import santander.cloud.sap.enums.Method;
import santander.cloud.sap.enums.PaymentStatus;


@Data
public class Payment {
    private Method method;
    private PaymentStatus paymentStatus;
}
