package com.quickMove.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    private String password;

    private String phone;

    private String role;
    @Column(unique = false, nullable = true)
    private String licenseNumber;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id", nullable = true)
    private VehicleType vehicleType;

    private String vehicleNumber;

    private String vehicleModel;

    private String vehicleColor;

    private Double latitude;

    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = true)
    @JsonIgnore
    private Organization organization;

    // Override UserDetails methods:

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // If the role is stored as a String, you can convert it to a SimpleGrantedAuthority.
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
        // Note: Roles should typically be prefixed with "ROLE_" in Spring Security, like "ROLE_USER" or "ROLE_ADMIN".
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
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
