package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santander.cloud.sap.enums.TitleSituationEnum;
import santander.cloud.sap.models.PaymentDda;

import java.util.List;

@Repository
public interface PaymentDdaRepository extends JpaRepository<PaymentDda, String> {

    List<PaymentDda> findByTitleSituationEnum(TitleSituationEnum titleSituationEnum);
}
