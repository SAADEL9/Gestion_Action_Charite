package ma.emsi.gestionactioncharite.service;
import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.StatutAction;
import java.util.List;
import java.util.Optional;

public interface ActionChariteService {
    ActionCharite save(ActionCharite action);

    Optional<ActionCharite> findById(Long id);

    List<ActionCharite> findAll();

    List<ActionCharite> findByStatut(String statut);

    ActionCharite update(Long id, ActionCharite action);

    ActionCharite publier(Long id);

    ActionCharite archiver(Long id);

    Double getProgression(Long id);

    void delete(Long id);
}