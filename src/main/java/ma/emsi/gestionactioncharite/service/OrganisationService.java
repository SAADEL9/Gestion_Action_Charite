package ma.emsi.gestionactioncharite.service;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.entity.User;
import java.util.List;
import java.util.Optional;

public interface OrganisationService {
    Organisation save(Organisation organisation);
    Organisation createOrganisation(User user, Organisation organisation);
    Optional<Organisation> findById(Long id);
    List<Organisation> findAll();
    Organisation update(Long id, Organisation organisation);
    Organisation valider(Long id);
    Organisation rejeter(Long id);
    void delete(Long id);
}
