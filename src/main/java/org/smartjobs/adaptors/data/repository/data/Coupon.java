package org.smartjobs.adaptors.data.repository.data;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;
    private String code;
    @Setter
    private boolean applied;
    private Date created;
    private boolean expired;
    private int value;
}
