package com.example.studyrats.controller;

import com.example.studyrats.dto.GroupResponseDTO;
import com.example.studyrats.dto.MembershipDTO;
import com.example.studyrats.model.User;
import com.example.studyrats.service.GroupMemberShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("groupmember")
public class GroupMemberShipController {

    @Autowired
    private GroupMemberShipService groupMemberShipService;

    @PostMapping("/join/{idUser}/{idGroup}")
    public ResponseEntity<CollectionModel<EntityModel<Optional<GroupResponseDTO>>>> joinMember(@PathVariable String idUser, @PathVariable String idGroup) {
        Optional<GroupResponseDTO> group = groupMemberShipService.addUserToGroup(idUser, idGroup);
        if (group.isEmpty()) {
            List<EntityModel<Optional<GroupResponseDTO>>> entities = List.of(EntityModel.of(group,
                    linkTo(methodOn(GroupMemberShipController.class).joinMember(idUser,idGroup )).withRel("self").withType("POST")

            ));
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .contentType(MediaTypes.HAL_JSON)
                    .body(CollectionModel.of(entities));
        }
        List<EntityModel<Optional<GroupResponseDTO>>> entities = List.of(EntityModel.of(group,
                linkTo(methodOn(GroupController.class).createGroup(idUser,null )).withRel("create_group").withType("POST"),
                linkTo(methodOn(GroupMemberShipController.class).joinMember(idUser,idGroup )).withRel("self").withType("POST")

        ));
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaTypes.HAL_JSON)
                .body(CollectionModel.of(entities));
    }
}
