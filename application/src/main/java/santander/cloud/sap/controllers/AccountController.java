package santander.cloud.sap.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import santander.cloud.sap.models.Account;
import santander.cloud.sap.service.AccountService;

@RestController
@CrossOrigin
/*@CrossOrigin(origins = {
        "https://hubDePocs-quiet-kob-rw.cfapps.us10-001.hana.ondemand.com/",
        "http://localhost:5500",
        "http://localhost:4200"
})*/
@RequiredArgsConstructor
@RequestMapping(value = "/account", produces = "application/json")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<Account> getBalance(@RequestParam(name = "branch", required = false) String branch,
                                              @RequestParam(name = "accountNumber", required = false) String numberAccount,
                                              @RequestParam(name = "bankId", required = false) String bankId) {
        return ResponseEntity.ok().body(accountService.getAccountBalance(branch, numberAccount, bankId));
    }
}