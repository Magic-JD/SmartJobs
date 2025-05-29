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


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id", nullable = false)
    @Setter
    private Cv cv;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @Setter
    private Role role;

    private Date lastAccessed;
}
