package com.app.lifetimefinancialplanner.domain.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_USER")
@SequenceGenerator(name = "SEQ_USER_GENERATOR", sequenceName = "SEQ_USER", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(generator = "SEQ_USER_GENERATOR")
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @CreationTimestamp
    @Column(name = "ins_date")
    private LocalDateTime createdAt = LocalDateTime.now();
}
