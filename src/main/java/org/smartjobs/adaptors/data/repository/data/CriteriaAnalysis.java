package org.smartjobs.adaptors.data.repository.data;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaAnalysis {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    @Setter
    private Analysis analysis;

    private double score;
    private long maxScore;
    private String criteriaRequest;
    private String description;
    private long userCriteriaId;

}
