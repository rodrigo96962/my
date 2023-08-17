package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.models.PixReceipt;

@Repository
public interface PixReceiptRepository extends JpaRepository<PixReceipt, String> {
}
