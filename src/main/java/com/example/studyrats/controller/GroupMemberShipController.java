package com.example.studyrats.controller;

import com.example.studyrats.service.GroupMemberShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("groupmember")
public class GroupMemberShipController {

    @Autowired
    private GroupMemberShipService groupMemberShipService;

    @PostMapping("/join/{idUser}/{idGroup}")
    public ResponseEntity<?> joinMember(@PathVariable String idUser, @PathVariable String idGroup) {
        if (groupMemberShipService.addUserToGroup(idUser, idGroup).isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
