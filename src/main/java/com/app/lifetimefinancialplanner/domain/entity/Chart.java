package com.app.lifetimefinancialplanner.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_CHART")
@SequenceGenerator(name = "SEQ_CHART_GENERATOR", sequenceName = "SEQ_CHART", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Chart {
    @Id
    @GeneratedValue(generator = "SEQ_CHART_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SIMULATION_ID", nullable = false)
    private Simulation simulation;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "CHART_TYPE", length = 50, nullable = false)
    private String chartType;

    @Column(columnDefinition = "CLOB", nullable = false)
    private String data;

    @CreationTimestamp
    @Column(name = "ins_date")
    private LocalDateTime createdAt = LocalDateTime.now();
}
