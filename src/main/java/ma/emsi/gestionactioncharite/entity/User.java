package ma.emsi.gestionactioncharite.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nom;
    private String prenom;
    
    @Column(unique = true, nullable = false)
    private String email;
    private String telephone;

    @Column(length = 500)
    private String adresse;
    private String motDePasse;
    private String photoProfil;
    private LocalDate dateInscription;
    @Builder.Default
    @Column(name = "is_actif",nullable = false, columnDefinition = "boolean default true")
    private boolean actif = true;
    @Builder.Default
    @Column(name = "organisation_request_pending",nullable = false, columnDefinition = "boolean default false")
    private boolean organisationRequestPending = false;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Don> dons = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "admin")
    @Builder.Default
    @JsonIgnore
    private List<Organisation> organisations = new ArrayList<>();
}
