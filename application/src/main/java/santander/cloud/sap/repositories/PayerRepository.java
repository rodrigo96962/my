package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.models.Payer;

@Repository
public interface PayerRepository extends JpaRepository<Payer, String> {
}
