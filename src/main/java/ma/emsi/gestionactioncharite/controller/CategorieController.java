package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.entity.Categorie;
import ma.emsi.gestionactioncharite.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("categories", categorieService.findAll());
        return "categorie/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categorie", new Categorie());
        return "categorie/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Categorie categorie) {
        categorieService.save(categorie);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        categorieService.findById(id).ifPresent(c -> model.addAttribute("categorie", c));
        return "categorie/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Categorie categorie) {
        categorieService.update(id, categorie);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categorieService.delete(id);
        return "redirect:/categories";
    }
}