package ma.emsi.gestionactioncharite.repository;

import ma.emsi.gestionactioncharite.entity.ActionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionImageRepository extends JpaRepository<ActionImage, Long> {
}
