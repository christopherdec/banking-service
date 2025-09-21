package org.acme.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.domain.Address;
import org.acme.domain.Agency;
import org.acme.domain.http.HttpAgency;
import org.acme.domain.http.RegistrationStatus;
import org.acme.exception.AgencyNotFoundException;
import org.acme.exception.InactiveAgencyException;
import org.acme.repository.AgencyRepository;
import org.acme.service.http.RegistrationStatusHttpService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@QuarkusTest
public class AgencyServiceTest {

    @InjectMock
    private AgencyRepository agencyRepository;

    @InjectMock
    @RestClient
    private RegistrationStatusHttpService registrationStatusHttpService;

    @Inject
    AgencyService agencyService;

    private Agency createAgency() {
        Address address = new Address();
        return new Agency(1L, "Agency", "Agency GmbH", "1234567890", address);
    }

    private HttpAgency createHttpAgency(RegistrationStatus registrationStatus) {
        return new HttpAgency("Agency", "Agency GmbH", "1234567890", registrationStatus);
    }

    @Test
    public void shouldNotRegisterWhenClientReturnsNull() {
        Agency agency = createAgency();
        when(registrationStatusHttpService.findByRegistrationNumber(agency.getRegistrationNumber())).thenReturn(null);
        assertThrows(AgencyNotFoundException.class, () -> agencyService.register(agency));
        verify(agencyRepository, never()).persist(agency);
    }

    @Test
    public void shouldNotRegisterWhenRegistrationStatusIsInactive() {
        HttpAgency httpAgency = createHttpAgency(RegistrationStatus.INACTIVE);
        Agency agency = createAgency();
        when(registrationStatusHttpService.findByRegistrationNumber(agency.getRegistrationNumber())).thenReturn(httpAgency);
        assertThrows(InactiveAgencyException.class, () -> agencyService.register(agency));
        verify(agencyRepository, never()).persist(agency);
    }

    @Test
    public void shouldRegisterWhenRegistrationStatusIsActive() throws Throwable {
        HttpAgency httpAgency = createHttpAgency(RegistrationStatus.ACTIVE);
        Agency agency = createAgency();
        when(registrationStatusHttpService.findByRegistrationNumber(agency.getRegistrationNumber())).thenReturn(httpAgency);
        agencyService.register(agency);
        verify(agencyRepository).persist(agency);
    }


}
