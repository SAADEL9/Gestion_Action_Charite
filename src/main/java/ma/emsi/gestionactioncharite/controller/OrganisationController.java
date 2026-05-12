package ma.emsi.gestionactioncharite.controller;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import ma.emsi.gestionactioncharite.service.UserService;
import ma.emsi.gestionactioncharite.service.ActionChariteService;
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
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/organisations")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;
    private final UserService userService;
    private final ActionChariteService actionChariteService;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("organisations", organisationService.findAll());
        return "organisation/list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal, Model model) {
        var organisation = organisationService.findById(id).orElse(null);
        model.addAttribute("organisation", organisation);
        if (organisation != null) {
            model.addAttribute("actions", actionChariteService.findByOrganisationId(id));
            model.addAttribute("canEdit", checkOwnership(organisation, principal));
        }
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
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) throws IOException {

        if (logoFile != null && !logoFile.isEmpty()) {
            organisation.setLogo(storeFile(logoFile, "logos"));
        }
        if (coverFile != null && !coverFile.isEmpty()) {
            organisation.setCoverImage(storeFile(coverFile, "covers"));
        }

        var user = userService.findEntityByEmail(principal.getUsername());
        organisationService.createOrganisation(user, organisation);
        return "redirect:/organisations";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal, Model model) {
        Organisation org = organisationService.findById(id).orElse(null);
        if (org == null) return "redirect:/organisations";
        
        if (!checkOwnership(org, principal)) {
            return "redirect:/organisations/" + id + "?error=unauthorized";
        }
        
        model.addAttribute("organisation", org);
        return "organisation/form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ORG_ADMIN', 'ADMIN')")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails principal,
                         @ModelAttribute Organisation organisation,
                         @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                         @RequestParam(value = "coverFile", required = false) MultipartFile coverFile) throws IOException {
        
        Organisation existing = organisationService.findById(id).orElse(null);
        if (existing == null || !checkOwnership(existing, principal)) {
            return "redirect:/organisations/" + id + "?error=unauthorized";
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            organisation.setLogo(storeFile(logoFile, "logos"));
        }
        if (coverFile != null && !coverFile.isEmpty()) {
            organisation.setCoverImage(storeFile(coverFile, "covers"));
        }
        organisationService.update(id, organisation);
        return "redirect:/organisations/" + id;
    }

    private boolean checkOwnership(Organisation org, UserDetails principal) {
        if (principal == null) return false;
        // Super admin can edit anything
        if (principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        // Check if user is the assigned admin of the org
        return org.getAdmin() != null && org.getAdmin().getEmail().equals(principal.getUsername());
    }

    @GetMapping("/valider/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String valider(@PathVariable Long id,
                          @RequestParam(required = false, defaultValue = "organisations") String from) {
        organisationService.valider(id);
        return "admin".equals(from) ? "redirect:/admin/dashboard" : "redirect:/organisations";
    }

    @GetMapping("/rejeter/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejeter(@PathVariable Long id,
                          @RequestParam(required = false, defaultValue = "organisations") String from) {
        organisationService.rejeter(id);
        return "admin".equals(from) ? "redirect:/admin/dashboard" : "redirect:/organisations";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        organisationService.delete(id);
        return "redirect:/organisations";
    }

    private String storeFile(MultipartFile file, String subDir) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads", subDir);
        Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + subDir + "/" + filename;
    }
}
