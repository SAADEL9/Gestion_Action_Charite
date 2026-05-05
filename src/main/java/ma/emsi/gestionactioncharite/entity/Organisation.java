package ma.emsi.gestionactioncharite.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "organisations")
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nom;
    private String email;
    private String telephone;
    private String adresse;
    
    private String adresseLegal;
    private String numeroFiscal;
    private String contactPrincipal;
    private String logo;
    
    @Column(length = 1000)
    private String description;
    
    private LocalDate dateCreation;
    
    @Enumerated(EnumType.STRING)
    private StatusOrganisation status;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ActionCharite> actions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
        if (status == null) {
            status = StatusOrganisation.PENDING;
        }
    }
}


