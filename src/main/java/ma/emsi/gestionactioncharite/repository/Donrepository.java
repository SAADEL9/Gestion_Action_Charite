package ma.emsi.gestionactioncharite.repository;

import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.entity.StatutDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface Donrepository extends JpaRepository<Don, Long> {
    List<Don> findByUserId(Long userId);

    List<Don> findByActionId(Long actionId);

    List<Don> findByStatus(StatutDon status);}
