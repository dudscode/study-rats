package com.example.studyrats.service;


import com.example.studyrats.dto.RankingDTO;
import com.example.studyrats.model.Checkin;
import com.example.studyrats.model.Group;
import com.example.studyrats.model.User;
import com.example.studyrats.repository.CheckinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckinService {

    @Autowired
    private CheckinRepository checkinRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    public boolean existGroupAndUser(String userId, String groupId){
        Optional<User> optionalUser = userService.getEntityById(userId);
        Optional<Group> optionalGroup = groupService.getEntityById(groupId);
        if(optionalUser.isEmpty() || optionalGroup.isEmpty()) {
            return false;
        }
        return optionalGroup.stream()
                .anyMatch(m -> m.getMemberships().stream()
                        .anyMatch(member -> member.getUser().getUserId().equals(userId)));

    }

    public boolean createCheckin(String userId, String groupId) {

        if (!existGroupAndUser(userId, groupId)) {
            return false;
        }

        Optional<User> optionalUser = userService.getEntityById(userId);
        Optional<Group> optionalGroup = groupService.getEntityById(groupId);
        Checkin checkin = Checkin.builder()
                .user(optionalUser.get())
                .group(optionalGroup.get())
                .checkinDate(LocalDateTime.now())
                .build();

        checkinRepository.save(checkin);

        return true;
    }

    public List<RankingDTO> getRanking( String groupId) {
        return checkinRepository.getRankingByGroup(groupId);
    }

}
