package ma.emsi.gestionactioncharite.repository;

import ma.emsi.gestionactioncharite.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface Participationrepository extends JpaRepository<Participation, Long> {
    List<Participation> findByUserId(Long userId);

    List<Participation> findByActionId(Long actionId);

    boolean existsByUserIdAndActionId(Long userId, Long actionId);
}
