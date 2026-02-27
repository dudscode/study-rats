package com.example.studyrats.service;

import com.example.studyrats.config.TokenConfig;
import com.example.studyrats.dto.LoginUserRequest;
import com.example.studyrats.dto.LoginUserResponse;
import com.example.studyrats.dto.RegisterUserResponse;
import com.example.studyrats.dto.UserResponseDTO;
import com.example.studyrats.mapper.UserMapper;
import com.example.studyrats.model.User;
import com.example.studyrats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenConfig tokenConfig;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> getUserById(String id) {
        return userRepository.findById(id)
                .map(UserMapper::toDTO);
    }

    public Optional<RegisterUserResponse> createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return Optional.empty();
        }
        user.setCreationDate(LocalDateTime.now());
        String passwordHash = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(passwordHash);
        User savedUser = userRepository.save(user);
        RegisterUserResponse userResponse = new RegisterUserResponse(savedUser.getFirstName(), savedUser.getEmail(), user.getUserId());
        return Optional.of(userResponse);
    }

    public Optional<User> getEntityById(String id) {
        return userRepository.findById(id);
    }


    public Optional<LoginUserResponse> loginUser(LoginUserRequest loginUserRequest) {
        User user = userRepository.findByEmail(loginUserRequest.email());

        if (user == null) {
            return Optional.empty();
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUserRequest.email(), loginUserRequest.password());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User authenticatedUser = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(authenticatedUser);

        if (authentication.isAuthenticated()) {
            LoginUserResponse loginResponse = new LoginUserResponse(user.getFirstName(),token , user.getUserId());
            return Optional.of(loginResponse);
        }
        return Optional.empty();
    }
}
