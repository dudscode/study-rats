package com.example.studyrats.controller;


import com.example.studyrats.dto.UserResponseDTO;
import com.example.studyrats.model.User;
import com.example.studyrats.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
   public ResponseEntity<User> create(@RequestBody User user) {
            if(userService.createUser(user)) {
                return  ResponseEntity.status(HttpStatus.CREATED).body(user);
            }
            return  ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
