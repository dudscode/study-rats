package com.example.studyrats.service;

import com.example.studyrats.dto.UserResponseDTO;
import com.example.studyrats.mapper.UserMapper;
import com.example.studyrats.model.Group;
import com.example.studyrats.model.GroupMembership;
import com.example.studyrats.model.User;
import com.example.studyrats.repository.GroupRepository;
import com.example.studyrats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private  UserRepository userRepository;

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

    public UserResponseDTO createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return null;
        }
        user.setCreationDate(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }

    public Optional<User> getEntityById(String id) {
        return userRepository.findById(id);
    }


}
