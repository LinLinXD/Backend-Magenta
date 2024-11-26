package com.magenta.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entidad para un usuario.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador del usuario

    @Column(nullable = false, unique = true)
    private String username; // Nombre de usuario

    @Column(nullable = false)
    private String password; // Contraseña

    @Column(nullable = false, unique = true)
    private String email; // Correo electrónico

    @Column(nullable = false)
    private String name; // Nombre

    private String phone; // Teléfono

    @Lob
    @Column(name = "profile_image", columnDefinition = "MEDIUMBLOB")
    private byte[] profileImage; // Imagen de perfil

    @Column(name = "image_content_type", length = 100)
    private String imageContentType; // Tipo de contenido de la imagen

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>(); // Roles del usuario

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<AppointmentEntity> appointments = new HashSet<>(); // Citas del usuario

    public void addAppointment(AppointmentEntity appointment) {
        appointments.add(appointment);
        appointment.setUser(this);
    }

    public void removeAppointment(AppointmentEntity appointment) {
        appointments.remove(appointment);
        appointment.setUser(null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}