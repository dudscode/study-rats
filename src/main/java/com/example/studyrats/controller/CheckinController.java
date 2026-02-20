package com.example.studyrats.controller;

import com.example.studyrats.model.Checkin;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("checkin")
public class CheckinController {

    @Autowired
    private CheckinService checkinService;

    @PostMapping("/{idUser}")
    public ResponseEntity<CollectionModel<EntityModel<Checkin>>> createCheckin(@PathVariable String idUser, @RequestBody Checkin checkin) {
        List<Checkin> createdCheckins = checkinService.createCheckin(idUser, checkin);

       if (!createdCheckins.isEmpty()) {
           return ResponseEntity.status(HttpStatus.CREATED)
                   .contentType(MediaTypes.HAL_JSON)
                   .body(CollectionModel.of(createdCheckins.stream().map(c -> EntityModel.of(c,
                           linkTo(methodOn(CheckinController.class).createCheckin(idUser, checkin)).withRel("self").withType("POST")
                   )).toList()
                   ));
       }
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
