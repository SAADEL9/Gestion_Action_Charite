package ma.emsi.gestionactioncharite.repository;

import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.StatutAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActionChariteRepository extends JpaRepository<ActionCharite, Long> {

    List<ActionCharite> findByStatus(StatutAction statut);

    List<ActionCharite> findByOrganisationId(Long organisationId);

    List<ActionCharite> findByCategorieId(Long categorieId);

    long countByOrganisationId(Long organisationId);

    List<ActionCharite> findTop5ByOrganisationIdOrderByDateCreationDescIdDesc(Long organisationId);
}
