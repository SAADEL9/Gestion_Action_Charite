package ma.emsi.gestionactioncharite.controller;
import ma.emsi.gestionactioncharite.entity.Organisation;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/organisations")
@RequiredArgsConstructor
public class OrganisationController {

    private final OrganisationService organisationService;

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
    public String showCreateForm(Model model) {
        model.addAttribute("organisation", new Organisation());
        return "organisation/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Organisation organisation) {
        organisationService.save(organisation);
        return "redirect:/organisations";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        organisationService.findById(id).ifPresent(o -> model.addAttribute("organisation", o));
        return "organisation/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Organisation organisation) {
        organisationService.update(id, organisation);
        return "redirect:/organisations";
    }

    @GetMapping("/valider/{id}")
    public String valider(@PathVariable Long id) {
        organisationService.valider(id);
        return "redirect:/organisations/en-attente";
    }

    @GetMapping("/rejeter/{id}")
    public String rejeter(@PathVariable Long id) {
        organisationService.rejeter(id);
        return "redirect:/organisations/en-attente";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        organisationService.delete(id);
        return "redirect:/organisations";
    }
}
