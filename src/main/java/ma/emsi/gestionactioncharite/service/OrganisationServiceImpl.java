package ma.emsi.gestionactioncharite.service;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.entity.Role;
import ma.emsi.gestionactioncharite.entity.StatusOrganisation;
import ma.emsi.gestionactioncharite.entity.User;
import ma.emsi.gestionactioncharite.repository.OrganisationRepository;
import ma.emsi.gestionactioncharite.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisationServiceImpl implements OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;

    @Override
    public Organisation save(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    @Override
    public Organisation createOrganisation(User user, Organisation organisation) {
        organisation.setAdmin(user);
        organisation.setStatus(StatusOrganisation.PENDING);
        organisation.setDateCreation(LocalDate.now());

        Organisation saved = organisationRepository.save(organisation);

        if (user.getRole() != Role.ADMIN) {
            user.setRole(Role.ORG_ADMIN);
            userRepository.save(user);
        }

        return saved;
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
        Organisation existing = organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée : " + id));

        existing.setNom(organisation.getNom());
        existing.setEmail(organisation.getEmail());
        existing.setTelephone(organisation.getTelephone());
        existing.setAdresse(organisation.getAdresse());
        existing.setAdresseLegal(organisation.getAdresseLegal());
        existing.setNumeroFiscal(organisation.getNumeroFiscal());
        existing.setContactPrincipal(organisation.getContactPrincipal());
        if (organisation.getLogo() != null && !organisation.getLogo().isBlank()) {
            existing.setLogo(organisation.getLogo());
        }
        existing.setDescription(organisation.getDescription());

        return organisationRepository.save(existing);
    }

    @Override
    public Organisation valider(Long id) {
        Organisation org = organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée : " + id));
        org.setStatus(StatusOrganisation.APPROVED);
        return organisationRepository.save(org);
    }

    @Override
    public Organisation rejeter(Long id) {
        Organisation org = organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée : " + id));
        org.setStatus(StatusOrganisation.REJECTED);
        return organisationRepository.save(org);
    }

    @Override
    public void delete(Long id) {
        organisationRepository.deleteById(id);
    }
}
