package org.smartjobs.com.repository.data;

import jakarta.persistence.*;
import lombok.*;

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
    @Getter
    @Setter
    private String fullText;

    @Lob
    @Getter
    @Setter
    private String condensedText;

    @Getter
    @Setter
    private String candidateName;

    @Getter
    @Setter
    private String filePath;
}
