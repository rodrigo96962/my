package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.models.DebitAccount;

import java.util.Optional;

@Repository
public interface DebitAccountRepository extends JpaRepository<DebitAccount, Long> {

    Optional<DebitAccount> findByBranchAndNumber(Long branch, Long number);


}
