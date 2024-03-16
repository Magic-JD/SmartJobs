package org.smartjobs.com.repository.data;

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

    @Lob
    @Setter
    private String fullText;

    @Lob
    @Setter
    private String condensedText;

    @Setter
    private String candidateName;

    @Setter
    private String filePath;
}
