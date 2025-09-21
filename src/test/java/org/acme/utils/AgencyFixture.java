package org.acme.utils;

import org.acme.domain.Address;
import org.acme.domain.Agency;
import org.acme.domain.http.HttpAgency;
import org.acme.domain.http.RegistrationStatus;

public class AgencyFixture {

    public static Agency createAgency() {
        Address address = new Address("123 Main St", "Floripa", "Santa Catarina", "88080000", "Brazil");
        return new Agency(1L, "Agency", "Agency GmbH", "1234567890", address);
    }

    public static HttpAgency createHttpAgency(RegistrationStatus registrationStatus) {
        return new HttpAgency("Agency", "Agency GmbH", "1234567890", registrationStatus);
    }
}
