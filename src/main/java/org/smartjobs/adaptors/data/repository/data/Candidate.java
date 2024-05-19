package org.smartjobs.adaptors.data.repository.data;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Boolean currentlySelected;

    @Setter
    private String name;

    private Long cvId;

    private Long userId;

    private Long roleId;

    private Date lastAccessed;
}
