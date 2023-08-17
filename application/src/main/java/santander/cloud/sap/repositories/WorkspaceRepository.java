package santander.cloud.sap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import santander.cloud.sap.models.Workspace;

import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, String> {

    Optional<Workspace> findFirstByOrderByIdDesc();
}
