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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    private Agency testAgency;
    private HttpAgency activeHttpAgency;
    private HttpAgency inactiveHttpAgency;

    @BeforeEach
    void setUp() {
        testAgency = createAgency();
        activeHttpAgency = createHttpAgency(RegistrationStatus.ACTIVE);
        inactiveHttpAgency = createHttpAgency(RegistrationStatus.INACTIVE);
    }

    private Agency createAgency() {
        Address address = new Address("123 Main St", "Floripa", "Santa Catarina", "88080000", "Brazil");
        return new Agency(1L, "Agency", "Agency GmbH", "1234567890", address);
    }

    private HttpAgency createHttpAgency(RegistrationStatus registrationStatus) {
        return new HttpAgency("Agency", "Agency GmbH", "1234567890", registrationStatus);
    }

    @Test
    void register_shouldThrowAgencyNotFoundException_whenExternalServiceReturnsNull() {
        when(registrationStatusHttpService.findByRegistrationNumber(testAgency.getRegistrationNumber())).thenReturn(null);
        assertThrows(AgencyNotFoundException.class, () -> agencyService.register(testAgency));
        verify(agencyRepository, never()).persist(testAgency);
    }

    @Test
    void register_shouldThrowInactiveAgencyException_whenRegistrationStatusIsInactive() {
        when(registrationStatusHttpService.findByRegistrationNumber(testAgency.getRegistrationNumber())).thenReturn(inactiveHttpAgency);
        assertThrows(InactiveAgencyException.class, () -> agencyService.register(testAgency));
        verify(agencyRepository, never()).persist(testAgency);
    }

    @Test
    void register_shouldPersistAgency_whenRegistrationStatusIsActive() {
        when(registrationStatusHttpService.findByRegistrationNumber(testAgency.getRegistrationNumber())).thenReturn(activeHttpAgency);
        assertDoesNotThrow(() -> agencyService.register(testAgency));
        verify(agencyRepository).persist(testAgency);
    }


}
