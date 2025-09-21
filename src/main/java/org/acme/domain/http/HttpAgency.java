package org.acme.domain.http;

import lombok.Getter;

@Getter
public class HttpAgency {
    private String name;
    private String registrationNumber;
    private String corporateName;
    private RegistrationStatus registrationStatus;
}
