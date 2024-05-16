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

    private Long definedCriteriaId;

    @Nullable
    private String value;

    private Integer score;

}
