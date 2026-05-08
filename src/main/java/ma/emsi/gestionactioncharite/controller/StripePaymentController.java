package ma.emsi.gestionactioncharite.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.entity.MethodePaiement;
import ma.emsi.gestionactioncharite.service.ActionChariteService;
import ma.emsi.gestionactioncharite.service.DonService;
import ma.emsi.gestionactioncharite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/actions")
@RequiredArgsConstructor
public class StripePaymentController {

    private final DonService donService;
    private final ActionChariteService actionChariteService;
    private final UserService userService;

    @Value("${stripe.currency:eur}")
    private String currency;

    @PostMapping("/{id}/stripe/create-intent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPaymentIntent(
            @PathVariable Long id,
            @RequestParam Double montant,
            @AuthenticationPrincipal UserDetails principal) {

        ActionCharite action = actionChariteService.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable : " + id));

        double objectif = action.getObjectifCollect() != null ? action.getObjectifCollect() : 0.0;
        double collecte = action.getSommeActuelle() != null ? action.getSommeActuelle() : 0.0;
        double reste = Math.max(0.0, objectif - collecte);
        if (objectif > 0 && montant > reste) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le montant dépasse ce qui reste à collecter."));
        }

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (montant * 100))
                    .setCurrency(currency)
                    .addPaymentMethodType("card")
                    .putMetadata("actionId", id.toString())
                    .putMetadata("userEmail", principal.getUsername())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            var user = userService.findEntityByEmail(principal.getUsername());
            Don don = Don.builder()
                    .user(user)
                    .actionCharite(action)
                    .montant(montant)
                    .methodePaiement(MethodePaiement.STRIPE)
                    .transactionId(paymentIntent.getId())
                    .build();
            Don savedDon = donService.saveEnAttente(don);

            return ResponseEntity.ok(Map.of(
                    "clientSecret", paymentIntent.getClientSecret(),
                    "donId", savedDon.getId()
            ));
        } catch (StripeException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/stripe/confirm/{donId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long id,
            @PathVariable Long donId) {

        Don don = donService.findById(donId)
                .orElseThrow(() -> new RuntimeException("Don introuvable : " + donId));

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(don.getTransactionId());

            if ("succeeded".equals(paymentIntent.getStatus())) {
                donService.confirmer(donId);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                donService.echouer(donId);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Paiement non confirmé par Stripe."));
            }
        } catch (StripeException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
