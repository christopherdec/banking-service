package org.acme.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String corporateName;
    private String registrationNumber;
    @Embedded
    private Address address;
}
