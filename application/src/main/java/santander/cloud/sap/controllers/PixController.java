package santander.cloud.sap.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import santander.cloud.sap.models.PixReceipt;
import santander.cloud.sap.models.PixRequest;
import santander.cloud.sap.service.PixService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pix")
@CrossOrigin(origins = {
        "https://hubDePocs-quiet-kob-rw.cfapps.us10-001.hana.ondemand.com/",
        "http://localhost:5500",
        "http://localhost:4200"
})
public class PixController {
    private static final Logger logger = LoggerFactory.getLogger(PixController.class);
    private final PixService pixService;

    @GetMapping
    public ResponseEntity<PixReceipt> getReceiptPix(@RequestParam(name = "pix_payment_id") @Valid @NotEmpty String pix_payment_id,
                                                    @RequestParam(name = "workspace_id", required = false) String workspace_id){
        return ResponseEntity.ok(pixService.getReceiptPix(pix_payment_id, workspace_id));
    }

    @PostMapping
    public ResponseEntity<PixReceipt> postPix(@RequestBody @Valid PixRequest pixRequest){
        return ResponseEntity.ok(pixService.postPix(pixRequest));
    }
}
