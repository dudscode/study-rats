package com.example.studyrats.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "Password is required")
    private String passwordHash;
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    private LocalDateTime creationDate;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference("user-membership")
    private List<GroupMembership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonBackReference("user-checkin")
    private List<Checkin> checkins = new ArrayList<>();

    public List<GroupMembership> getMemberships() {
        if (memberships == null) {
            memberships = new ArrayList<>();
        }
        return memberships;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
