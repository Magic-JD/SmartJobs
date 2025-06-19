package org.smartjobs.adaptors.data.repository.data;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "candidate_role",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    private Date lastAccessed;
}
