package ma.emsi.gestionactioncharite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String message;
    private boolean lu;
    private LocalDateTime dateEnvoi;
    private String email;

    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    // ── Relations ──

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionCharite actionCharite;
}