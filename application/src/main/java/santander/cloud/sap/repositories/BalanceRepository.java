package santander.cloud.sap.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.models.Balance;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Integer> {

}
