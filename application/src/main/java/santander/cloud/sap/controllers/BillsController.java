package santander.cloud.sap.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import santander.cloud.sap.models.Bill;
import santander.cloud.sap.service.BillsService;

import java.util.List;

@RestController
@CrossOrigin(origins = {
        "https://hubDePocs-quiet-kob-rw.cfapps.us10-001.hana.ondemand.com/",
        "http://localhost:5500",
        "http://localhost:4200"
})
@RequestMapping(value = "/bills", produces = "application/json")
public class BillsController {

    private final BillsService billsService;

    public BillsController(BillsService billsService) {
        this.billsService = billsService;
    }

    @GetMapping
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok().body(billsService.getAll());
    }

    @GetMapping(value = "/open")
    public ResponseEntity<List<Bill>> getOpen() {
        return ResponseEntity.ok().body(billsService.getOpen());
    }

    @GetMapping(value = "/closed")
    public ResponseEntity<List<Bill>> getClosed() {
        return ResponseEntity.ok().body(billsService.getClosed());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable String id) {
        return ResponseEntity.ok().body(billsService.getById(id));
    }
}
