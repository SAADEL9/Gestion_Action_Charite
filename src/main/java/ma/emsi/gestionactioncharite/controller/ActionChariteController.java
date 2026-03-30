package ma.emsi.gestionactioncharite.controller;
import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.StatutAction;
import ma.emsi.gestionactioncharite.service.ActionChariteService;
import ma.emsi.gestionactioncharite.service.OrganisationService;
import ma.emsi.gestionactioncharite.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class ActionChariteController {

    private final ActionChariteService actionChariteService;
    private final OrganisationService organisationService;
    private final CategorieService categorieService;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("actions", actionChariteService.findAll());
        return "action/list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        actionChariteService.findById(id).ifPresent(a -> {
            model.addAttribute("action", a);
            model.addAttribute("progression", actionChariteService.getProgression(id));
        });
        return "action/detail";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("action", new ActionCharite());
        model.addAttribute("organisations", organisationService.findAll());
        model.addAttribute("categories", categorieService.findAll());
        return "action/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ActionCharite action) {
        actionChariteService.save(action);
        return "redirect:/actions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        actionChariteService.findById(id).ifPresent(a -> model.addAttribute("action", a));
        model.addAttribute("organisations", organisationService.findAll());
        model.addAttribute("categories", categorieService.findAll());
        return "action/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ActionCharite action) {
        actionChariteService.update(id, action);
        return "redirect:/actions";
    }

    @GetMapping("/publier/{id}")
    public String publier(@PathVariable Long id) {
        actionChariteService.publier(id);
        return "redirect:/actions";
    }

    @GetMapping("/archiver/{id}")
    public String archiver(@PathVariable Long id) {
        actionChariteService.archiver(id);
        return "redirect:/actions";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        actionChariteService.delete(id);
        return "redirect:/actions";
    }
}
