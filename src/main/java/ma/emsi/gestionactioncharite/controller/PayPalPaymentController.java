package ma.emsi.gestionactioncharite.controller;

import jakarta.servlet.http.HttpServletRequest;
import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.entity.MethodePaiement;
import ma.emsi.gestionactioncharite.service.ActionChariteService;
import ma.emsi.gestionactioncharite.service.DonService;
import ma.emsi.gestionactioncharite.service.PayPalService;
import ma.emsi.gestionactioncharite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/actions")
@RequiredArgsConstructor
public class PayPalPaymentController {

    private final PayPalService payPalService;
    private final DonService donService;
    private final ActionChariteService actionChariteService;
    private final UserService userService;

    @PostMapping("/{id}/paypal/create-order")
    @PreAuthorize("isAuthenticated()")
    public String createOrder(@PathVariable Long id,
                              @RequestParam(name = "montant", required = false) String montantStr,
                              @AuthenticationPrincipal UserDetails principal,
                              HttpServletRequest servletRequest) {

        double montant;
        try {
            if (montantStr == null || montantStr.isBlank()) return "redirect:/actions/" + id + "?don=error";
            montant = Double.parseDouble(montantStr.replace(",", ".").trim());
            if (montant <= 0) return "redirect:/actions/" + id + "?don=error";
        } catch (NumberFormatException e) {
            return "redirect:/actions/" + id + "?don=error";
        }

        var action = actionChariteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable : " + id));

        double objectif = action.getObjectifCollect() != null ? action.getObjectifCollect() : 0.0;
        double collecte = action.getSommeActuelle() != null ? action.getSommeActuelle() : 0.0;
        if (objectif > 0 && montant > Math.max(0.0, objectif - collecte) + 0.005) {
            return "redirect:/actions/" + id + "?don=error";
        }

        try {
            var user = userService.findEntityByEmail(principal.getUsername());
            Don don = Don.builder()
                    .user(user)
                    .actionCharite(action)
                    .montant(montant)
                    .methodePaiement(MethodePaiement.PAYPAL)
                    .transactionId("pending")
                    .build();
            Don savedDon = donService.saveEnAttente(don);

            String base = servletRequest.getScheme() + "://"
                    + servletRequest.getServerName() + ":" + servletRequest.getServerPort();
            Map<String, Object> order = payPalService.createOrder(
                    montant,
                    base + "/actions/" + id + "/paypal/success?donId=" + savedDon.getId(),
                    base + "/actions/" + id + "/paypal/cancel"
            );

            savedDon.setTransactionId((String) order.get("id"));
            donService.saveEnAttente(savedDon);

            return "redirect:" + payPalService.getApprovalUrl(order);

        } catch (Exception e) {
            return "redirect:/actions/" + id + "?don=paypal-error";
        }
    }

    @GetMapping("/{id}/paypal/success")
    @PreAuthorize("isAuthenticated()")
    public String success(@PathVariable Long id,
                          @RequestParam String token,
                          @RequestParam Long donId,
                          @RequestParam(required = false) String PayerID) {
        try {
            if (payPalService.captureOrder(token)) {
                donService.confirmer(donId);
                return "redirect:/actions/" + id + "?don=payment-success";
            }
        } catch (Exception ignored) {
        }
        donService.echouer(donId);
        return "redirect:/actions/" + id + "?don=paypal-error";
    }

    @GetMapping("/{id}/paypal/cancel")
    public String cancel(@PathVariable Long id) {
        return "redirect:/actions/" + id + "?don=error";
    }
}
