package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.entity.User;
import ma.emsi.gestionactioncharite.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findEntityByEmail(email);
        return new CustomUserDetails(user); // ← this is the only change
    }
}