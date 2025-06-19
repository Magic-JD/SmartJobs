package org.smartjobs.adaptors.data.repository.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defined_criteria_id", nullable = false)
    private DefinedCriteria definedCriteria;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @Nullable
    private String value;

    private Integer score;

}
