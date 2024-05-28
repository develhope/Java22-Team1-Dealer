package com.develhope.spring.user.entities;


import com.develhope.spring.user.entities.Enum.UserTypes;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "User name")
    private String name;
    @Column(nullable = false, name = "User surname")
    private String surname;
    @Column(name = "User phoneNumber", unique = true)
    private String phoneNumber;
    @Column(nullable = false, unique = true, name = "User email")
    private String email;
    @Column(nullable = false, name = "User password")
    private String password;
    @Column(nullable = false, name = "User type")
    @Enumerated(EnumType.STRING)
    private UserTypes userType;

    public UserEntity(Long id, String name, String surname, String phoneNumber, String email, String password, UserTypes userType) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }
    public UserEntity( String name, String surname, String phoneNumber, String email, String password, UserTypes userType) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userType.name()));
    }

    @Override
    public String getUsername() {
        return email;
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
