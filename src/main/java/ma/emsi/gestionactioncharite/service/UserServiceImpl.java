package ma.emsi.gestionactioncharite.service;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.dto.UserRequestDTO;
import ma.emsi.gestionactioncharite.dto.UserResponseDTO;
import ma.emsi.gestionactioncharite.entity.Role;
import ma.emsi.gestionactioncharite.entity.User;
import ma.emsi.gestionactioncharite.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        return toDTO(user);
    }

    @Override
    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + email));
        return toDTO(user);
    }

    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));

        if (dto.getNom() != null) user.setNom(dto.getNom());
        if (dto.getPrenom() != null) user.setPrenom(dto.getPrenom());
        if (dto.getTelephone() != null) user.setTelephone(dto.getTelephone());
        if (dto.getAdresse() != null) user.setAdresse(dto.getAdresse());
        if (dto.getPhotoProfil() != null) user.setPhotoProfil(dto.getPhotoProfil());

        if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
            user.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }

        return toDTO(userRepository.save(user));
    }

    @Override
    public void deactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        user.setActif(false);
        userRepository.save(user);
    }

    @Override
    public void activate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        user.setActif(true);
        userRepository.save(user);
    }

    @Override
    public void changeRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        user.setRole(role);
        if (role != Role.USER) {
            user.setOrganisationRequestPending(false);
        }
        userRepository.save(user);
    }

    @Override
    public void requestOrganisationAccess(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        if (user.getRole() != Role.USER) {
            throw new IllegalStateException("Seul un utilisateur standard peut demander un accès organisation");
        }
        user.setOrganisationRequestPending(true);
        userRepository.save(user);
    }

    @Override
    public void approveOrganisationAccess(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        user.setRole(Role.ORG_ADMIN);
        user.setOrganisationRequestPending(false);
        userRepository.save(user);
    }

    private UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .adresse(user.getAdresse())
                .photoProfil(user.getPhotoProfil())
                .dateInscription(user.getDateInscription())
                .organisationRequestPending(user.isOrganisationRequestPending())
                .role(user.getRole())
                .actif(user.isActif())
                .build();
    }

    @Override
    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

