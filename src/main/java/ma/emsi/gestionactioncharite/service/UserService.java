package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.dto.UserRequestDTO;
import ma.emsi.gestionactioncharite.dto.UserResponseDTO;
import ma.emsi.gestionactioncharite.entity.Role;
import ma.emsi.gestionactioncharite.entity.User;

import java.util.List;

public interface UserService {
    UserResponseDTO findById(Long id);
    UserResponseDTO findByEmail(String email);
    List<UserResponseDTO> findAll();
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void deactivate(Long id);
    void activate(Long id);
    void changeRole(Long id, Role role);
    User findEntityByEmail(String email);
    User save(User user);
    boolean existsByEmail(String email);
}