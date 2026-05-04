package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.entity.StatutDon;
import ma.emsi.gestionactioncharite.repository.Donrepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonImpl implements DonService {

    private final Donrepository donRepository;

    @Override
    @Transactional
    public Don save(Don don) {
        don.setDateDon(LocalDate.now());
        don.setStatus(StatutDon.EN_ATTENTE);
        return donRepository.save(don);
    }

    @Override
    public Optional<Don> findById(Long id) {
        return donRepository.findById(id);
    }

    @Override
    public List<Don> findAll() {
        return donRepository.findAll();
    }

    @Override
    public List<Don> findByUserId(Long userId) {
        return donRepository.findByUserId(userId);
    }

    @Override
    public List<Don> findByActionChariteId(Long actionId) {
        return donRepository.findByActionChariteId(actionId);
    }

    @Override
    @Transactional
    public Don confirmer(Long id) {
        Don don = donRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Don non trouvé : " + id));
        don.setStatus(StatutDon.CONFIRME);
        return donRepository.save(don);
    }

    @Override
    @Transactional
    public Don rembourser(Long id) {
        Don don = donRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Don non trouvé : " + id));
        if (don.getStatus() != StatutDon.CONFIRME) {
            throw new RuntimeException("Seul un don confirmé peut être remboursé");
        }
        don.setStatus(StatutDon.REMBOURSE);
        return donRepository.save(don);
    }

    @Override
    @Transactional
    public Don echouer(Long id) {
        Don don = donRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Don non trouvé : " + id));
        don.setStatus(StatutDon.ECHOUE);
        return donRepository.save(don);
    }

    @Override
    public void delete(Long id) {
        donRepository.deleteById(id);
    }
}