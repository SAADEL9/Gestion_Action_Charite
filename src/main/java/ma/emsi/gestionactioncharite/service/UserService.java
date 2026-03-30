package ma.emsi.gestionactioncharite.service;



import ma.emsi.gestionactioncharite.entity.User;
import java.util.List;
import java.util.Optional;
public interface UserService {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User update(Long id, User user);
    void delete(Long id);
}
