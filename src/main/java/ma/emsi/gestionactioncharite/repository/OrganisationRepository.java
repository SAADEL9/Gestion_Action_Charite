package ma.emsi.gestionactioncharite.repository;

import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.entity.StatusOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation , Long> {
    List<Organisation> findByAdminId(Long adminId);
    List<Organisation> findByStatus(StatusOrganisation status);
}
