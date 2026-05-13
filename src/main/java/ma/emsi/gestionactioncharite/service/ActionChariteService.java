package ma.emsi.gestionactioncharite.service;
import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.StatutAction;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface ActionChariteService {
    ActionCharite save(ActionCharite action, MultipartFile imageFile);

    Optional<ActionCharite> findById(Long id);

    List<ActionCharite> findAll();

    List<ActionCharite> findByOrganisationId(Long organisationId);

    ActionCharite update(Long id, ActionCharite action, MultipartFile imageFile);

    ActionCharite publier(Long id);

    ActionCharite archiver(Long id);

    Double getProgression(Long id);

    void delete(Long id);
}