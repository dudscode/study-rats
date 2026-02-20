package com.example.studyrats.controller;

import com.example.studyrats.dto.GroupResponseDTO;
import com.example.studyrats.model.Checkin;
import com.example.studyrats.model.Group;
import com.example.studyrats.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("checkin")
public class CheckinController {

    @Autowired
    private CheckinService checkinService;

    @PostMapping("/{idUser}/{idGroup}")
    public ResponseEntity<Link>createCheckin(@PathVariable String idUser, @PathVariable String idGroup) {

       if (checkinService.createCheckin(idUser, idGroup)) {
           return ResponseEntity.status(HttpStatus.CREATED)
                   .contentType(MediaTypes.HAL_JSON)
                   .body(linkTo(methodOn(GroupController.class).getById(idUser, idGroup)).withRel("grupo").withType("GET"));
       }
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
