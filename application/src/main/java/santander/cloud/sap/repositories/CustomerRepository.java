package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import santander.cloud.sap.models.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}