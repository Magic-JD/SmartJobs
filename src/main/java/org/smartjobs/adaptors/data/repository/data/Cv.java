package org.smartjobs.adaptors.data.repository.data;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cv {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String fileHash;

    @Setter
    private Boolean currentlySelected;

    @Lob
    @Setter
    private String condensedText;

    @Setter
    private String candidateName;

    private Long userId;

    private Long roleId;
}
