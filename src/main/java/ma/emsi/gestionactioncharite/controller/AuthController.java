package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.dto.UserRequestDTO;
import ma.emsi.gestionactioncharite.entity.Role;
import ma.emsi.gestionactioncharite.entity.User;
import ma.emsi.gestionactioncharite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRequestDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRequestDTO dto, Model model) {

        // Check duplicate email
        if (userService.existsByEmail(dto.getEmail())) {
            model.addAttribute("errorEmail", "Cet email est déjà utilisé.");
            model.addAttribute("user", dto);
            return "auth/register";
        }

        User user = User.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(dto.getMotDePasse()))
                .telephone(dto.getTelephone())
                .role(Role.USER)
                .actif(true)
                .build();
        // dateInscription auto-set by @PrePersist

        userService.save(user);
        return "redirect:/login?registered=true";
    }
}
