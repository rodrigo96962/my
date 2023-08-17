package santander.cloud.sap.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import santander.cloud.sap.enums.TitleSituationEnum;
import santander.cloud.sap.models.*;
import santander.cloud.sap.service.PaymentService;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@CrossOrigin
/*@CrossOrigin(origins = {
        "https://hubDePocs-quiet-kob-rw.cfapps.us10-001.hana.ondemand.com/",
        "http://localhost:5500",
        "http://localhost:4200"
})*/
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/dda")
    public ResponseEntity<PaymentDdaResponse> getDda(@RequestParam(name = "originAggregates", required = false) List<String> originAggregates,
                                                     @RequestParam(name = "originAuthorized", required = false) List<String> originAuthorized,
                                                     @RequestParam(name = "beneficiariesDocument", required = false) List<String> beneficiaryDocument,
                                                     @RequestParam(name = "titleSituation", required = false) TitleSituationEnum titleSituationEnum,
                                                     @RequestParam(name = "initialDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date initialDate,
                                                     @RequestParam(name = "finalDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date finalDate,
                                                     @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                                     @RequestParam(name = "pageLimit", required = false) Integer pageLimit,
                                                     @RequestParam(name = "workspaceId", required = false) String workspaceId) {
        return ResponseEntity.ok(paymentService.getDda(originAggregates, originAuthorized, beneficiaryDocument, titleSituationEnum, initialDate, finalDate, pageNumber, pageLimit, workspaceId));
    }

    @PostMapping
    public ResponseEntity<PaymentReceipt> postPayment(@RequestBody @Valid PaymentRequest paymentRequest){
        return ResponseEntity.ok(paymentService.postPayment(paymentRequest));
    }

    @GetMapping("/{barCode}")
    public ResponseEntity<PaymentReceipt> getPayment(@PathVariable String barCode){
        return ResponseEntity.ok(paymentService.getPayment(barCode));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<PaymentReceipt>> postPayment(@RequestBody @Valid List<PaymentRequest> paymentRequestList,
                                                            @RequestParam(name = "sleepSeconds", required = false, defaultValue = "15") int sleepTime) throws InterruptedException {
        return ResponseEntity.ok(paymentService.postBatchPayment(paymentRequestList, sleepTime));
    }

    @PostMapping("/destinationTest")
    public ResponseEntity<String> getDestinationData(@RequestParam String destinationName) {
        return ResponseEntity.ok(paymentService.getDestinationData(destinationName));
    }
}
