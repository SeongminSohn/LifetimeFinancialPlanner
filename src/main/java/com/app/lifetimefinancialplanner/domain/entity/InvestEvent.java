package com.app.lifetimefinancialplanner.domain.entity;

import com.app.lifetimefinancialplanner.domain.embeddable.AllocationEmbeddable;
import com.app.lifetimefinancialplanner.domain.embeddable.DistributionEmbeddable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity @Table(name = "TBL_INVEST_EVENT")
@Getter @ToString(exclude = {"scenario", "investmentType"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder(toBuilder = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InvestEvent {
    @Id
    @Column(name = "EVENT_SERIES_ID")
    private Long eventSeriesId;

    // GPT helped me how to use List as a data type (collectionTable)
    @ElementCollection
    @CollectionTable(name = "TBL_INVEST_EVENT_ALLOCATION", joinColumns = @JoinColumn(name = "EVENT_SERIES_ID"))
    private List<AllocationEmbeddable> assetAllocations;

    @Column(name = "MAX_CASH", nullable = false)
    private Double maxCash;

    @OneToOne
    @MapsId
    @JoinColumn(name = "EVENT_SERIES_ID")
    private EventSeries eventSeries;

    @PrePersist
    private void onPrePersist() {
        if (this.eventSeries != null) {
            this.eventSeriesId = this.eventSeries.getId();
        }
    }
}
