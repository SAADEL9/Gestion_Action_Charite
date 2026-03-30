package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.entity.Categorie;
import java.util.List;
import java.util.Optional;

public interface CategorieService {
    Categorie save(Categorie categorie);
    Optional<Categorie> findById(Long id);
    List<Categorie> findAll();
    Categorie update(Long id, Categorie categorie);
    void delete(Long id);
}
