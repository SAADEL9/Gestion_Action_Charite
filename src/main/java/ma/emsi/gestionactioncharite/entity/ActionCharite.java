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
@Table(name = "action_charite")
public class ActionCharite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateCreation;
    private String lieu;
    private Double objectifCollect;
    private Double sommeAcuelle;

    @Enumerated(EnumType.STRING)
    private StatutAction status;

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
}
