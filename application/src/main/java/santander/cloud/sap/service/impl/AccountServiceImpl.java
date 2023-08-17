package santander.cloud.sap.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import santander.cloud.sap.enums.PayloadTypeEnum;
import santander.cloud.sap.exceptions.NotFoundException;
import santander.cloud.sap.externalLib.SantanderOpenAPIConfig;
import santander.cloud.sap.models.Account;
import santander.cloud.sap.models.DebitAccount;
import santander.cloud.sap.repositories.AccountRepository;
import santander.cloud.sap.service.AccountService;
import santander.cloud.sap.service.DebitAccountService;

import javax.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import static java.util.Objects.isNull;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository repository;
    private final SantanderOpenAPIConfig santanderOpenAPIConfig;
    private final DebitAccountService debitAccountService;

    @Override
    public Account getAccountBalance(String branch, String numberAccount, String bankId) {
        Account balanceResponse;
        try {

            int minBranchLength = 4;
            int minAccountNumberLength = 12;
            logger.info("Validating Account Data...");
            if(isNull(branch)){
                branch = santanderOpenAPIConfig.getMainDebitAccountBranch();
            } else if(branch.length() < minBranchLength) {
                branch = StringUtils.repeat("0", minBranchLength - branch.length())
                        .concat(branch);
            }

            if(isNull(numberAccount)){
                numberAccount = santanderOpenAPIConfig.getMainDebitAccountNumber();
            } else if(numberAccount.length() < minAccountNumberLength) {
                numberAccount = StringUtils.repeat("0",minAccountNumberLength - numberAccount.length())
                        .concat(numberAccount);
            }

            if(isNull(bankId)){
                bankId = santanderOpenAPIConfig.getDefaultBankId();
            }
            logger.info("Branch: {} ; Number Account: {} ; Bank ID: {}", branch, numberAccount, bankId);

            Map<PayloadTypeEnum, HashMap<String, String>> payloadMap = new HashMap<>();
            String balanceId = branch.concat(".").concat(numberAccount);
            String url = santanderOpenAPIConfig.getBalanceUrlGet(bankId, balanceId);

            logger.info("Calling get balance api... URL = {}", url);
            String result = santanderOpenAPIConfig.executeHttpRequest(payloadMap, RequestMethod.GET, url);
            balanceResponse = new Gson().fromJson(result, Account.class);
            logger.info("Get balance api call done successfully, respose = {}", result);
            updateLocalBalance(balanceResponse, branch, numberAccount);

        } catch (Exception e) {
            logger.error("Error getting balance from OpenAPI: " + e.getMessage());
            balanceResponse = repository.findByDebitAccountBranchAndDebitAccountNumber(Long.parseLong(branch), Long.parseLong(numberAccount))
                    .orElseThrow(NotFoundException::new);
            balanceResponse.setUpdated(false);
        }

        return balanceResponse;
    }

    @Override
    public Account debitAccountBalance(Double value) {
        Account balance =  getAccountBalance(null, null, null);
        if (!balance.isUpdated()){
            Double accountBalance = balance.getAvailableAmount() - value;
            balance.setAvailableAmount(accountBalance);
            repository.save(balance);
        }

        return balance;
    }

    @Override
    public Boolean hasBalance(Double value) {
        Account balance =  getAccountBalance(null, null, null);

        return balance.getAvailableAmount() - value > 0;
    }

    public void updateLocalBalance(Account newBalance, String branch, String numberAccount) {
        logger.info("Updating local balance... branch = {} ; account = {}", branch, numberAccount);
        Long branchLong = Long.parseLong(branch);
        Long numberAccountLong = Long.parseLong(numberAccount);
        Optional<Account> idOptional = repository.findByDebitAccountBranchAndDebitAccountNumber(branchLong, numberAccountLong);
        logger.info("Balance ID found = {}", idOptional.orElse(null));
        Long debitAccountId = debitAccountService.getEntityId(branchLong, numberAccountLong);

        if(idOptional.isPresent())
            newBalance.setId(idOptional.get().getId());

        newBalance.setDebitAccount(new DebitAccount(debitAccountId, branchLong, numberAccountLong));
        newBalance.setUpdated(true);

        logger.info("New Balance to persist: {}", newBalance);

        repository.save(newBalance);
    }
}
