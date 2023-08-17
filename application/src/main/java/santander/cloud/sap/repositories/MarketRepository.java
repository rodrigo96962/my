package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.models.Market;

@Repository
public interface MarketRepository extends JpaRepository<Market, String> {
}
