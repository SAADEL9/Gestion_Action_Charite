package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.repository.ActionChariteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActionChariteServiceImpl implements ActionChariteService {

    private final ActionChariteRepository actionChariteRepository;

    @Override
    public ActionCharite save(ActionCharite action) {
        return actionChariteRepository.save(action);
    }

    @Override
    public Optional<ActionCharite> findById(Long id) {
        return actionChariteRepository.findById(id);
    }

    @Override
    public List<ActionCharite> findAll() {
        return actionChariteRepository.findAll();
    }

    @Override
    public List<ActionCharite> findByStatut(String statut) {
        return actionChariteRepository.findByStatus(statut);
    }

    @Override
    public ActionCharite update(Long id, ActionCharite action) {
        action.setId(id);
        return actionChariteRepository.save(action);
    }

    @Override
    public ActionCharite publier(Long id) {
        ActionCharite action = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée : " + id));
        action.setStatus("PUBLIEE");
        return actionChariteRepository.save(action);
    }

    @Override
    public ActionCharite archiver(Long id) {
        ActionCharite action = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée : " + id));
        action.setStatus("ARCHIVEE");
        return actionChariteRepository.save(action);
    }

    @Override
    public Double getProgression(Long id) {
        ActionCharite action = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée : " + id));
        if (action.getObjectifCollect() == null || action.getObjectifCollect() == 0) return 0.0;
        return (action.getSommeAcuelle() / action.getObjectifCollect()) * 100;
    }

    @Override
    public void delete(Long id) {
        actionChariteRepository.deleteById(id);
    }
}