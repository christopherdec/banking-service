package org.acme.domain;

import lombok.Getter;

@Getter
public class Agency {

    private Long id;
    private String name;
    private String corporateName;
    private String registrationNumber;
    private Address address;
}
