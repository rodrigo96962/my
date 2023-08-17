package santander.cloud.sap.utils;

import org.springframework.stereotype.Service;
import santander.cloud.sap.enums.BillStatus;
import santander.cloud.sap.models.*;
import santander.cloud.sap.models.json.BillJson;

import java.util.Date;

import static java.util.UUID.randomUUID;

@Service
public class JsonMapper {
    public static Bill toModel(BillJson billJson) {
        Bill bill = Bill.builder()
                .id(billJson.getId().split("-")[0])
                .billingType(billJson.getBillingType())
                .billStatus(GetBillStatus.random())
                .dueDate(billJson.getDueDate())
                .createInvoice(billJson.isCreateInvoice())
                .market(billJson.getMarket())
                .customer(billJson.getCustomer())
                .payer(billJson.getPayer())
                .currency(billJson.getNetAmount().getCurrency())
                .netAmount(GetBillNetAmount.random())
                .build();
        GetReceipt.standard(bill);
        return bill;
    }
}

class GetReceipt {
    static void standard(Bill bill) {
        Double paymentValue = GetBillNetAmount.random();

        Beneficiary ben = Beneficiary.builder()
                .bankCode("02038232")
                .branch(0756L)
                .documentNumber("58425453062")
                .documentType("CPF")
                .ispb("9b88df74")
                .name("Thaís Barbosa Rocha")
                .type("CONTA CORRENTE")
                .build();

        DebitAccount deb = DebitAccount.builder()
                .branch(00013L)
                .number(16052L)
                .build();

        Transaction trans = Transaction.builder()
                .code("be732d52")
                .date(new Date())
                .endToEnd("E9040088820230323230900012711237")
                .value(paymentValue)
                .build();

        if (bill.getBillStatus() == BillStatus.CLOSED) {
            bill.setPixReceipt(PixReceipt.builder()
                                .id(randomUUID().toString())
                                .beneficiary(ben)
                                .debitAccount(deb)
                                .nominalValue(paymentValue)
                                .payer(bill.getPayer())
                                .paymentValue(paymentValue)
                                .totalValue(paymentValue)
                                .transaction(trans)
                                .build());
        }
    }
}

class GetBillStatus {
    static BillStatus random() {
        return (int)(Math.random()*15) % 2 == 0 ? BillStatus.OPEN : BillStatus.CLOSED;
    }
}

class GetBillNetAmount {
    static Double random() {
        Double[] values = new Double[] {
                550D, 700D, 100D, 500D, 230D, 1300D, 600D, 400D
        };
        int random = (int) (Math.random() * 8);
        return values[random];
    }
}