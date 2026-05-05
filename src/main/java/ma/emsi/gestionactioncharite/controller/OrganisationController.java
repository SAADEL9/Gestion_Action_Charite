package ma.emsi.gestionactioncharite.controller;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import ma.emsi.gestionactioncharite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/organisations")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;
    private final UserService userService;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("organisations", organisationService.findAll());
        return "organisation/list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        organisationService.findById(id).ifPresent(o -> model.addAttribute("organisation", o));
        return "organisation/detail";
    }


    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("organisation", new Organisation());
        return "organisation/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String create(
            @AuthenticationPrincipal UserDetails principal,
            @ModelAttribute Organisation organisation,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile) throws IOException {

        // Handle logo upload
        if (logoFile != null && !logoFile.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + logoFile.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/logos/");
            Files.createDirectories(uploadPath);
            Files.copy(logoFile.getInputStream(), uploadPath.resolve(filename));
            organisation.setLogo("/uploads/logos/" + filename);
        }

        var user = userService.findEntityByEmail(principal.getUsername());
        organisationService.createOrganisation(user, organisation);
        return "redirect:/organisations";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        organisationService.findById(id).ifPresent(o -> model.addAttribute("organisation", o));
        return "organisation/form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String update(@PathVariable Long id, @ModelAttribute Organisation organisation) {
        organisationService.update(id, organisation);
        return "redirect:/organisations";
    }

    @GetMapping("/valider/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String valider(@PathVariable Long id) {
        organisationService.valider(id);
        return "redirect:/organisations";
    }

    @GetMapping("/rejeter/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejeter(@PathVariable Long id) {
        organisationService.rejeter(id);
        return "redirect:/organisations";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        organisationService.delete(id);
        return "redirect:/organisations";
    }
}
