package com.example.studyrats.controller;

import com.example.studyrats.service.CheckinService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/{idUser}/{idGroup}")
    public ResponseEntity<List<Link>>createCheckin(@PathVariable String idUser, @PathVariable String idGroup) {

       if (checkinService.createCheckin(idUser, idGroup)) {
           return ResponseEntity.status(HttpStatus.CREATED)
                   .contentType(MediaTypes.HAL_JSON)
                   .body(List.of(
                           linkTo(methodOn(GroupController.class).getById(idUser, idGroup)).withRel("grupo").withType("GET"),
                           linkTo(methodOn(CheckinController.class).createCheckin(idUser, idGroup)).withRel("self").withType("POST"),
                           linkTo(methodOn(GroupController.class).getRanking(idGroup)).withRel("ranking").withType("GET")
                   ));
       }
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
