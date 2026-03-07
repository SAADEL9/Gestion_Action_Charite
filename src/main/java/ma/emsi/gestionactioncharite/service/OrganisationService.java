package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.repository.OrganisationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganisationService {
    private final OrganisationRepository organisationRepository;
    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }
    public Organisation createOrganisation(Organisation organistion)
    {
        return organisationRepository.save(organistion);
    }
    public Optional<List<Organisation>> getAllOrganisations()
    {
        return Optional.of(organisationRepository.findAll());
    }
}
