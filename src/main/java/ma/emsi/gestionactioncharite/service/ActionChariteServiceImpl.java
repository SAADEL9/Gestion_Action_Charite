package ma.emsi.gestionactioncharite.service;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.Categorie;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.entity.StatutAction;
import ma.emsi.gestionactioncharite.entity.StatusOrganisation;
import ma.emsi.gestionactioncharite.repository.ActionChariteRepository;
import ma.emsi.gestionactioncharite.repository.CategorieRepository;
import ma.emsi.gestionactioncharite.repository.OrganisationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActionChariteServiceImpl implements ActionChariteService {

    private final ActionChariteRepository actionChariteRepository;
    private final OrganisationRepository organisationRepository;
    private final CategorieRepository categorieRepository;

    @Override
    public ActionCharite save(ActionCharite action) {
        action.setOrganisation(resolveApprovedOrganisation(action));
        action.setCategorie(resolveCategorie(action));
        if (action.getDateCreation() == null) {
            action.setDateCreation(LocalDate.now());
        }
        if (action.getSommeActuelle() == null) {
            action.setSommeActuelle(0.0);
        }
        if (action.getStatus() == null) {
            action.setStatus(StatutAction.BROUILLON);
        }
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
    public List<ActionCharite> findByOrganisationId(Long organisationId) {
        return actionChariteRepository.findByOrganisationId(organisationId);
    }

    @Override
    public ActionCharite update(Long id, ActionCharite action) {
        ActionCharite existing = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée : " + id));

        existing.setTitre(action.getTitre());
        existing.setDescription(action.getDescription());
        existing.setDateDebut(action.getDateDebut());
        existing.setDateFin(action.getDateFin());
        existing.setLieu(action.getLieu());
        existing.setObjectifCollect(action.getObjectifCollect());
        existing.setStatus(action.getStatus() != null ? action.getStatus() : existing.getStatus());
        existing.setOrganisation(resolveApprovedOrganisation(action));
        existing.setCategorie(resolveCategorie(action));

        if (existing.getDateCreation() == null) {
            existing.setDateCreation(LocalDate.now());
        }
        if (existing.getSommeActuelle() == null) {
            existing.setSommeActuelle(0.0);
        }

        return actionChariteRepository.save(existing);
    }

    @Override
    public ActionCharite publier(Long id) {
        ActionCharite action = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée : " + id));
        action.setStatus(StatutAction.PUBLIEE);
        return actionChariteRepository.save(action);
    }

    @Override
    public ActionCharite archiver(Long id) {
        ActionCharite action = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée : " + id));
        action.setStatus(StatutAction.ARCHIVEE);
        return actionChariteRepository.save(action);
    }

    @Override
    public Double getProgression(Long id) {
        ActionCharite action = actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action non trouvée : " + id));
        if (action.getObjectifCollect() == null || action.getObjectifCollect() == 0) {
            return 0.0;
        }
        return (action.getSommeActuelle() / action.getObjectifCollect()) * 100;
    }

    @Override
    public void delete(Long id) {
        actionChariteRepository.deleteById(id);
    }

    private Organisation resolveApprovedOrganisation(ActionCharite action) {
        if (action.getOrganisation() == null || action.getOrganisation().getId() == null) {
            throw new IllegalArgumentException("Une organisation est requise pour créer une action");
        }

        Organisation organisation = organisationRepository.findById(action.getOrganisation().getId())
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée : " + action.getOrganisation().getId()));

        if (organisation.getStatus() != StatusOrganisation.APPROVED) {
            throw new IllegalStateException("Création d'actions interdite : organisation non approuvée");
        }

        return organisation;
    }

    private Categorie resolveCategorie(ActionCharite action) {
        if (action.getCategorie() == null || action.getCategorie().getId() == null) {
            throw new IllegalArgumentException("Une catégorie est requise pour créer une action");
        }

        return categorieRepository.findById(action.getCategorie().getId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée : " + action.getCategorie().getId()));
    }
}
