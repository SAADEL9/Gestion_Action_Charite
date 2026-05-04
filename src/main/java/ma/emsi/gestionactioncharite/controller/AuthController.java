package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.entity.Role;
import ma.emsi.gestionactioncharite.entity.User;
import ma.emsi.gestionactioncharite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        user.setRole(Role.USER);
        user.setDateInscription(LocalDate.now());
        userService.save(user); // ← uses your existing service
        return "redirect:/login?registered=true";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}