package ma.emsi.gestionactioncharite.service;
import ma.emsi.gestionactioncharite.entity.Categorie;
import ma.emsi.gestionactioncharite.repository.CategorieRepository;
import ma.emsi.gestionactioncharite.service.CategorieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;

    @Override
    public Categorie save(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Override
    public Optional<Categorie> findById(Long id) {
        return categorieRepository.findById(id);
    }

    @Override
    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    @Override
    public Categorie update(Long id, Categorie categorie) {
        categorie.setId(id);
        return categorieRepository.save(categorie);
    }

    @Override
    public void delete(Long id) {
        categorieRepository.deleteById(id);
    }
}