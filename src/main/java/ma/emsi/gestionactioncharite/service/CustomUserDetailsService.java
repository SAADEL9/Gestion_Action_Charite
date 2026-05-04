package ma.emsi.gestionactioncharite.service;

import ma.emsi.gestionactioncharite.entity.User;
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

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getMotDePasse())
                .roles(user.getRole().name())
                .build();
    }
}