package ma.emsi.gestionactioncharite.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Entity
@Data
@Getter @Setter
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String adresseLegal;
    private String numeroFiscal;
    private String contactPrincipal;
    private String logo ;
    private String description;
    private LocalDate dateCreation;
    private StatusOrganisation status;
}
