package ma.emsi.gestionactioncharite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "participations")
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO → IDENTITY
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    private boolean present;

    @ManyToOne(fetch = FetchType.LAZY)    // relation manquante ajoutée
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)    // relation manquante ajoutée
    @JoinColumn(name = "action_id", nullable = false)
    private ActionCharite action;
}