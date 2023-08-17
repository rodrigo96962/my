package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.models.PartialPayment;

@Repository
public interface PartialPaymentRepository extends JpaRepository<PartialPayment, Long> {
}
