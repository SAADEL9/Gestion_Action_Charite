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
@Table(name = "action_charite")
public class ActionCharite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titre;
    
    @Column(length = 1000)
    private String description;
    
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateCreation;
    private String lieu;
    private Double objectifCollect;
    
    @Builder.Default
    private Double sommeActuelle = 0.0;

    @Enumerated(EnumType.STRING)
    private StatutAction status;

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
    
    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Don> dons = new ArrayList<>();

    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "actionCharite", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}

