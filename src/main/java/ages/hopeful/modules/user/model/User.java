package ages.hopeful.modules.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_usuario_cpf", columnNames = "cpf")
})
public class User implements UserDetails {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id = UUID.randomUUID();

    @Column(name = "nome", nullable = false, length = 255)
    private String name;

    @Column(name = "cpf", nullable = false, length = 20)
    private String cpf;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "telefone", length = 20)
    private String phone;

    @Column(name = "senha", nullable = false, length = 255)
    private String password;

    @Column(name = "servico_id", columnDefinition = "uuid")
    private UUID serviceId;

    @Column(name = "cidade_id", columnDefinition = "uuid")
    private UUID cityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "papel_id", nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
