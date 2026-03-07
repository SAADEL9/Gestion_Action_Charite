package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organisation")
public class OrganisationController {
    private final OrganisationService organisationService;
    public OrganisationController(OrganisationService organisationService) {
        this.organisationService = organisationService;
    }
    @PostMapping("/create")
    public ResponseEntity<Organisation> createOrg(@RequestBody Organisation organisation)
    {
        return new ResponseEntity<>(organisationService.createOrganisation(organisation), HttpStatus.CREATED);
    }
}
