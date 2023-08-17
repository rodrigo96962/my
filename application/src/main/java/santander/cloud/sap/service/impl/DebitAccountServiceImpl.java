package santander.cloud.sap.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import santander.cloud.sap.models.DebitAccount;
import santander.cloud.sap.repositories.DebitAccountRepository;
import santander.cloud.sap.service.DebitAccountService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DebitAccountServiceImpl implements DebitAccountService {

    private static final Logger logger = LoggerFactory.getLogger(DebitAccountServiceImpl.class);

    private final DebitAccountRepository debitAccountRepository;

    @Override
    public void setEntityId(DebitAccount debitAccount) {
        Optional<DebitAccount> debitAccountOptional = debitAccountRepository
                .findByBranchAndNumber(debitAccount.getBranch(), debitAccount.getNumber());

        if (debitAccountOptional.isPresent()){
            debitAccount.setId(debitAccountOptional.get().getId());
        } else {
            debitAccountRepository.save(debitAccount);
        }
    }

    @Override
    public void setEntityIdList(List<DebitAccount> debitAccountList) {
        debitAccountList.forEach( debitAccount -> setEntityId(debitAccount));
    }

    @Override
    public Long getEntityId(Long branch, Long numberAccount) {
        logger.info("Getting DebitAccountId, branch = {}, account = {}", branch, numberAccount);
        Optional<DebitAccount> debitAccountIdOptional = debitAccountRepository.findByBranchAndNumber(branch, numberAccount);
        logger.info("Debit Account ID found = {}", debitAccountIdOptional.orElse(null));

        if(debitAccountIdOptional.isPresent())
            return debitAccountIdOptional.get().getId();

        DebitAccount debitAccount = new DebitAccount(branch, numberAccount);
        return debitAccountRepository.save(debitAccount).getId();
    }
}
