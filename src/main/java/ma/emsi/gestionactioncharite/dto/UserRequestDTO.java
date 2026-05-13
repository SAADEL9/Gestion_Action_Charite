package ma.emsi.gestionactioncharite.dto;

import lombok.*;
import ma.emsi.gestionactioncharite.entity.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String adresse;
    private String photoProfil;
    private Role role;
    private Boolean actif;
}