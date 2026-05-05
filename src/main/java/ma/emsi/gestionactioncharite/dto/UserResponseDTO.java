package ma.emsi.gestionactioncharite.dto;

import lombok.*;
import ma.emsi.gestionactioncharite.entity.Role;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String photoProfil;
    private LocalDate dateInscription;
    private boolean organisationRequestPending;
    private Role role;
    private boolean actif;
}
