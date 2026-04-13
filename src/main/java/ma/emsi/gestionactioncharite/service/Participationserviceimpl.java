package ma.emsi.gestionactioncharite.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import ma.emsi.gestionactioncharite.entity.Participation;
import ma.emsi.gestionactioncharite.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor

public class Participationserviceimpl implements ParticipationService{
    private final ParticipationRepository participationRepository;

    @Override
    @Transactional
    public Participation inscrire(Participation participation) {
        Long userId = participation.getUser().getId();
        Long actionId = participation.getAction().getId();

        if (participationRepository.existsByUserIdAndActionId(userId, actionId)) {
            throw new RuntimeException("L'utilisateur est déjà inscrit à cette action");
        }

        participation.setDateInscription(LocalDateTime.now());
        participation.setPresent(false);
        return participationRepository.save(participation);
    }

    @Override
    public Optional<Participation> findById(Long id) {
        return participationRepository.findById(id);
    }

    @Override
    public List<Participation> findAll() {
        return participationRepository.findAll();
    }

    @Override
    public List<Participation> findByUserId(Long userId) {
        return participationRepository.findByUserId(userId);
    }

    @Override
    public List<Participation> findByActionId(Long actionId) {
        return participationRepository.findByActionId(actionId);
    }

    @Override
    @Transactional
    public Participation marquerPresence(Long id, boolean present) {
        Participation participation = participationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participation non trouvée : " + id));
        participation.setPresent(present);
        return participationRepository.save(participation);
    }

    @Override
    public void delete(Long id) {
        participationRepository.deleteById(id);
    }
}
