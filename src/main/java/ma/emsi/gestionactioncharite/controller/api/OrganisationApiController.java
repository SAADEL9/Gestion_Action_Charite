package ma.emsi.gestionactioncharite.controller.api;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.repository.OrganisationRepository;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import ma.emsi.gestionactioncharite.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organisations")
@RequiredArgsConstructor
public class OrganisationApiController {

    private final OrganisationService organisationService;
    private final OrganisationRepository organisationRepository;
    private final UserService userService;

    @GetMapping
    public List<Organisation> listAll() {
        return organisationService.findAll();
    }

    @GetMapping("/me")
    public List<Organisation> myOrganisations(@AuthenticationPrincipal UserDetails principal) {
        var user = userService.findEntityByEmail(principal.getUsername());
        return organisationRepository.findByAdminId(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public ResponseEntity<Organisation> create(@AuthenticationPrincipal UserDetails principal,
                                              @RequestBody Organisation organisation) {
        var user = userService.findEntityByEmail(principal.getUsername());
        return ResponseEntity.ok(organisationService.createOrganisation(user, organisation));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public Organisation approve(@PathVariable Long id) {
        return organisationService.valider(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        organisationService.rejeter(id);
        return ResponseEntity.noContent().build();
    }
}
