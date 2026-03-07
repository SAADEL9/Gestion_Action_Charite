package ma.emsi.gestionactioncharite.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Data
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom ;
    private String prenom;
    private String email;
    private String motDePasse;
    private String photoProfil;
    private LocalDate dateInscription;
    private Role role;
}
