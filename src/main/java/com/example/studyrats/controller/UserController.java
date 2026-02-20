package com.example.studyrats.controller;


import com.example.studyrats.dto.GroupResponseDTO;
import com.example.studyrats.dto.UserResponseDTO;
import com.example.studyrats.model.Group;
import com.example.studyrats.model.User;
import com.example.studyrats.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
   public ResponseEntity<EntityModel<UserResponseDTO>> create(@RequestBody User user) {

            Optional<UserResponseDTO> createdUser = userService.createUser(user);
            if(createdUser.isPresent()) {
                UserResponseDTO userDTO = createdUser.get();
                return ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaTypes.HAL_JSON)
                        .body(EntityModel.of(userDTO,
                                linkTo(methodOn(GroupController.class).createGroup(userDTO.getId(),null )).withRel("create_group").withType("POST"),
                                linkTo(methodOn(GroupMemberShipController.class).joinMember(userDTO.getId(),null )).withRel("join_group").withType("POST")

                        ));
            }
            return  ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
