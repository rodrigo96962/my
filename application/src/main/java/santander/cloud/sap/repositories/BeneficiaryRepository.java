package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.models.Beneficiary;

import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    Optional<Beneficiary> findByDocumentNumberAndDocumentType(String documentNumber, String documentType);

}
