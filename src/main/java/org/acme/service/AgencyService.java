package org.acme.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
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

    private final MeterRegistry meterRegistry;

    @Transactional
    @Timed("agency_register_timer")
    public void register(Agency agency) throws InactiveAgencyException, AgencyNotFoundException {
        HttpAgency httpAgency = registrationStatusHttpService.findByRegistrationNumber(agency.getRegistrationNumber());

        if (httpAgency == null) {
            Log.warn("Not found agency by registrationNumber " + agency.getRegistrationNumber());
            meterRegistry.counter(AgencyMetrics.NOT_FOUND_COUNTER.getValue()).increment();
            throw new AgencyNotFoundException();
        }

        if (httpAgency.getRegistrationStatus() == RegistrationStatus.INACTIVE) {
            Log.warn("Agency with registrationNumber " + agency.getRegistrationNumber() + " is inactive");
            meterRegistry.counter(AgencyMetrics.INACTIVE_COUNTER.getValue()).increment();
            throw new InactiveAgencyException();
        }

        agencyRepository.persist(agency);
        Log.info("Persisted " + agency);
        meterRegistry.counter(AgencyMetrics.ADDED_COUNTER.getValue()).increment();
    }

    public Optional<Agency> findById(Long id) {
        return Optional.ofNullable(agencyRepository.findById(id));
    }

    @Transactional
    public void deleteById(Long id) {
        Log.info("Deleting agency by id " + id);
        boolean deleted = agencyRepository.deleteById(id);
        if (deleted) {
            meterRegistry.counter(AgencyMetrics.DELETED_COUNTER.getValue()).increment();
        } else {
            meterRegistry.counter(AgencyMetrics.DELETE_FAILED_COUNTER.getValue()).increment();
        }
    }

    @Transactional
    public void update(Agency agency) throws AgencyNotFoundException, InactiveAgencyException {
        Log.info("Updating " + agency);
        agencyRepository.update(
                "name = ?1, corporateName = ?2, registrationNumber = ?3 where id = ?4",
                agency.getName(), agency.getCorporateName(), agency.getRegistrationNumber(), agency.getId()
        );
        meterRegistry.counter(AgencyMetrics.UPDATED_COUNTER.getValue()).increment();
    }
}
