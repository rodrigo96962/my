package santander.cloud.sap.service.impl;

import org.springframework.stereotype.Service;
import santander.cloud.sap.enums.BillStatus;
import santander.cloud.sap.exceptions.NotFoundException;
import santander.cloud.sap.models.Bill;
import santander.cloud.sap.models.PixReceipt;
import santander.cloud.sap.repositories.BillRepository;
import santander.cloud.sap.service.BillsService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillsServiceImpl implements BillsService {

    private final BillRepository billRepository;

    public BillsServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    @Override
    public Bill getById(String id) {
        return billRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Bill> getOpen() {
        return billRepository.findAll()
                .stream()
                .filter(x -> x.getBillStatus() == BillStatus.OPEN)
                .collect(Collectors.toList());
    }

    @Override
    public List<Bill> getClosed() {
        return billRepository.findAll()
                .stream()
                .filter(x -> x.getBillStatus() == BillStatus.CLOSED)
                .collect(Collectors.toList());
    }

    public void savePixReceiptByBillId(PixReceipt pixReceipt, String billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(NotFoundException::new);
        bill.setPixReceipt(pixReceipt);
        bill.setBillStatus(BillStatus.CLOSED);
        billRepository.save(bill);
    }
}
