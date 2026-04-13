package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.entity.Participation;

import java.util.List;
import java.util.Optional;

public interface ParticipationService {
    Participation inscrire(Participation participation);

    Optional<Participation> findById(Long id);

    List<Participation> findAll();

    List<Participation> findByUserId(Long userId);

    List<Participation> findByActionId(Long actionId);

    Participation marquerPresence(Long id, boolean present);

    void delete(Long id);
}
