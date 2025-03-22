package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.compositePk.AssetAllocationItemId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import javax.persistence.*;

@Entity @Table(name = "TBL_ASSET_ALLOCATION_ITEM")
@Data @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetAllocationItem {
    @EmbeddedId
    private AssetAllocationItemId id;

    @Column(nullable = false)
    private Double percentage;
}
