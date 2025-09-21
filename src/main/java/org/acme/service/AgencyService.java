package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.Agency;
import org.acme.domain.http.HttpAgency;
import org.acme.domain.http.RegistrationStatus;
import org.acme.exception.AgencyNotFoundException;
import org.acme.exception.InactiveAgencyException;
import org.acme.service.http.RegistrationStatusHttpService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AgencyService {

    @RestClient
    private RegistrationStatusHttpService registrationStatusHttpService;

    // temporary in-memory database
    private List<Agency> agencies = new ArrayList<>();

    public void register(Agency agency) throws InactiveAgencyException, AgencyNotFoundException {
        HttpAgency httpAgency = registrationStatusHttpService.findByRegistrationNumber(agency.getRegistrationNumber());

        if (httpAgency == null)
            throw new AgencyNotFoundException();

        if (httpAgency.getRegistrationStatus() == RegistrationStatus.INACTIVE)
            throw new InactiveAgencyException();

        agencies.add(agency);
    }

    public Optional<Agency> findById(Long id) {
        return agencies.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public void deleteById(Long id) {
        agencies.removeIf(a -> a.getId().equals(id));
    }

    public void update(Agency agency) throws AgencyNotFoundException, InactiveAgencyException {
        deleteById(agency.getId());
        register(agency);
    }
}
