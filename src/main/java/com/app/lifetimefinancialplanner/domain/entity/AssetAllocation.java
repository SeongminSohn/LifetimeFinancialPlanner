package com.app.lifetimefinancialplanner.domain.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "TBL_ASSET_ALLOCATION")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(name = "SEQ_ASSET_ALLOCATION_GENERATOR", sequenceName = "SEQ_ASSET_ALLOCATION", allocationSize = 1)
public class AssetAllocation {
    @Id
    @GeneratedValue(generator = "SEQ_ASSET_ALLOCATION_GENERATOR")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
