package com.develhope.spring.User.Entities;


import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.order.Entities.OrderEntity;
import jakarta.persistence.*;
import lombok.*;
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
@AllArgsConstructor
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
    private UserTypes userType;

    @Column(name = "user's orders")
    @OneToMany(mappedBy = "orderBuyer", fetch = FetchType.EAGER)
    private List<OrderEntity> orderEntities;

    @Column(name = "user's purchases")
    @OneToMany(mappedBy = "purchaseBuyer", fetch = FetchType.EAGER)
    private List<PurchaseEntity> purchaseEntities;

    @Column(name = "user's rents")
    @OneToMany(mappedBy = "userEntity", fetch = FetchType.EAGER)
    private List<RentEntity> rentEntities;

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
