package ma.emsi.gestionactioncharite.repository;

import ma.emsi.gestionactioncharite.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation , Long> {
}
