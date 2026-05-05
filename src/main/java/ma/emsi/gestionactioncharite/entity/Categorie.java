package ma.emsi.gestionactioncharite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String description;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ActionCharite> actions = new ArrayList<>();
}

