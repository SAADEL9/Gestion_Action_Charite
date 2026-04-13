package ma.emsi.gestionactioncharite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data                    // @Getter et @Setter sont inclus dans @Data — supprimés
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dons")
public class Don {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double montant;

    private LocalDate dateDon;

    @Enumerated(EnumType.STRING)   // était un String brut — remplacé par l'enum
    private StatutDon status;

    private String transactionId;

    @Enumerated(EnumType.STRING)   // champ manquant ajouté
    private MethodePaiement methodePaiement;

    @ManyToOne(fetch = FetchType.LAZY)    // relation manquante ajoutée
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)    // relation manquante ajoutée
    @JoinColumn(name = "action_id", nullable = false)
    private ActionCharite action;
}