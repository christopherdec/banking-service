package org.acme.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.acme.domain.Agency;
import org.acme.domain.http.HttpAgency;
import org.acme.domain.http.RegistrationStatus;
import org.acme.exception.AgencyNotFoundException;
import org.acme.exception.InactiveAgencyException;
import org.acme.repository.AgencyRepository;
import org.acme.service.http.RegistrationStatusHttpService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class AgencyService {

    @RestClient
    private RegistrationStatusHttpService registrationStatusHttpService;

    private final AgencyRepository agencyRepository;

    @Transactional
    public void register(Agency agency) throws InactiveAgencyException, AgencyNotFoundException {
        HttpAgency httpAgency = registrationStatusHttpService.findByRegistrationNumber(agency.getRegistrationNumber());

        if (httpAgency == null) {
            Log.warn("Not found agency by registrationNumber " + agency.getRegistrationNumber());
            throw new AgencyNotFoundException();
        }

        if (httpAgency.getRegistrationStatus() == RegistrationStatus.INACTIVE) {
            Log.warn("Agency with registrationNumber " + agency.getRegistrationNumber() + " is inactive");
            throw new InactiveAgencyException();
        }

        agencyRepository.persist(agency);
        Log.info("Persisted " + agency);
    }

    public Optional<Agency> findById(Long id) {
        return Optional.ofNullable(agencyRepository.findById(id));
    }

    @Transactional
    public void deleteById(Long id) {
        Log.info("Deleting agency by id " + id);
        agencyRepository.deleteById(id);
    }

    @Transactional
    public void update(Agency agency) throws AgencyNotFoundException, InactiveAgencyException {
        Log.info("Updating " + agency);
        agencyRepository.update(
                "name = ?1, corporateName = ?2, registrationNumber = ?3 where id = ?4",
                agency.getName(), agency.getCorporateName(), agency.getRegistrationNumber(), agency.getId()
        );
    }
}
