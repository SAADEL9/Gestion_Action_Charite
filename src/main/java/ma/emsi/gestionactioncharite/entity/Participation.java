package ma.emsi.gestionactioncharite.entity;
import jakarta.persistence.*;
import lombok.Data;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    private boolean present;
}
