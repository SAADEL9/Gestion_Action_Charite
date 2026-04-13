package ma.emsi.gestionactioncharite.service;
import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.entity.StatutDon;

import java.util.List;
import java.util.Optional;

public interface Don {
    Don save(Don don);

    Optional<Don> findById(Long id);

    List<Don> findAll();

    List<Don> findByUserId(Long userId);

    List<Don> findByActionId(Long actionId);

    Don confirmer(Long id);

    Don rembourser(Long id);

    Don echouer(Long id);

    void delete(Long id);
}

