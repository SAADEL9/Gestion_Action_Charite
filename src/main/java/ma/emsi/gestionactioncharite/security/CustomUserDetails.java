package ma.emsi.gestionactioncharite.security;

import ma.emsi.gestionactioncharite.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String motDePasse;
    private final String nom;
    private final String prenom;
    private final boolean actif;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.motDePasse = user.getMotDePasse();
        this.nom = user.getNom();
        this.prenom = user.getPrenom();
        this.actif = user.isActif();
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getFullName() { 
        String fullName = ((prenom != null ? prenom : "") + " " + (nom != null ? nom : "")).trim();
        return fullName.isEmpty() ? email : fullName;
    }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return motDePasse; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isEnabled() { return actif; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}