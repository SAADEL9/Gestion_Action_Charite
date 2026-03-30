package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.repository.OrganisationRepository;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisationServiceImpl implements OrganisationService {

    private final OrganisationRepository organisationRepository;

    @Override
    public Organisation save(Organisation organisation) {

        return organisationRepository.save(organisation);
    }

    @Override
    public Optional<Organisation> findById(Long id) {
        return organisationRepository.findById(id);
    }

    @Override
    public List<Organisation> findAll() {
        return organisationRepository.findAll();
    }

    @Override
    public Organisation update(Long id, Organisation organisation) {
        organisation.setId(id);
        return organisationRepository.save(organisation);
    }

    @Override
    public Organisation valider(Long id) {
        Organisation org = organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée : " + id));

        return organisationRepository.save(org);
    }

    @Override
    public Organisation rejeter(Long id) {
        Organisation org = organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée : " + id));

        return organisationRepository.save(org);
    }

    @Override
    public void delete(Long id) {
        organisationRepository.deleteById(id);
    }
}