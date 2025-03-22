package com.app.lifetimefinancialplanner.domain.compositePk;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class AssetAllocationItemId implements Serializable {

    @Column(name = "ASSET_ALLOCATION_ID")
    private Long assetAllocationId;

    @Column(name = "INVESTMENT_ID")
    private Long investmentId;

    public AssetAllocationItemId(Long assetAllocationId, Long investmentId) {
        this.assetAllocationId = assetAllocationId;
        this.investmentId = investmentId;
    }
}
