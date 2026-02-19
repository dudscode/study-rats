package com.example.studyrats.controller;


import com.example.studyrats.dto.GroupResponseDTO;
import com.example.studyrats.model.Group;
import com.example.studyrats.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("groups")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @PostMapping("/create/{idUser}")
    public ResponseEntity<?> createGroup(@PathVariable String idUser, @RequestBody Group group) {
        Optional<GroupResponseDTO> groupOptional = groupService.save(idUser, group);

        if (groupOptional.isPresent()) {
            return new ResponseEntity<>(groupOptional.get(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @GetMapping("/all")
    public ResponseEntity<List<?>> getAllGroups() {
        return new ResponseEntity<>(groupService.findAll(), HttpStatus.OK);
    }
}
