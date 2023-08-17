package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.models.Bill;

import java.util.Optional;


@Repository
public interface BillRepository extends JpaRepository<Bill, String> {

}
