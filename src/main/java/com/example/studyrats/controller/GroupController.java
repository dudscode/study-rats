package com.example.studyrats.controller;


import com.example.studyrats.dto.GroupResponseDTO;
import com.example.studyrats.model.Group;
import com.example.studyrats.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.MediaTypes;

@RestController
@RequestMapping("groups")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping("/create/{idUser}")
    public ResponseEntity<CollectionModel<EntityModel<GroupResponseDTO>>> createGroup(@PathVariable String idUser, @RequestBody Group group) {
        Optional<GroupResponseDTO> groupOptional = groupService.save(idUser, group);

        if (groupOptional.isPresent()) {
            GroupResponseDTO groupResponseDTO = groupOptional.get();
            List<EntityModel<GroupResponseDTO>> entities = List.of(EntityModel.of(groupResponseDTO,
                    linkTo(methodOn(GroupController.class).getById(idUser,group.getId())).withRel("grupo").withType("GET")
            ));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaTypes.HAL_JSON)
                    .body(CollectionModel.of(entities));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/all")
    public ResponseEntity<List<GroupResponseDTO>> getAllGroups() {
        return new ResponseEntity<>(groupService.findAll(), HttpStatus.OK);
    }

    @GetMapping("{idUser}/{idGroup}")
    public ResponseEntity<CollectionModel<EntityModel<GroupResponseDTO>>> getById(@PathVariable String idUser, @PathVariable String idGroup) {
        Optional<GroupResponseDTO> groupOptional = groupService.findById(idUser, idGroup);
        if (groupOptional.isPresent()) {
            GroupResponseDTO groupResponseDTO = groupOptional.get();
            List<EntityModel<GroupResponseDTO>> entities = List.of(EntityModel.of(groupResponseDTO,
                    linkTo(methodOn(GroupController.class).getById(idUser, idGroup)).withRel("self").withType("GET"),
                    linkTo(methodOn(GroupController.class).createGroup(idUser,null )).withRel("create_group").withType("POST")
                    ));
            return ResponseEntity.status(HttpStatus.FOUND)
                    .contentType(MediaTypes.HAL_JSON)
                    .body(CollectionModel.of(entities));
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
