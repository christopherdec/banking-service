package org.acme.domain.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpAgency {
    private String name;
    private String registrationNumber;
    private String corporateName;
    private RegistrationStatus registrationStatus;
}
