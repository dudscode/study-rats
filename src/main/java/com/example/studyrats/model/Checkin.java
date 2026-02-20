package com.example.studyrats.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checkin implements Serializable {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private LocalDateTime checkinDate;
}
