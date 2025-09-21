package org.acme.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
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
