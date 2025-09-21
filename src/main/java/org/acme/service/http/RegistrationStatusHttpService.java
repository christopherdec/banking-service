package org.acme.service.http;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.acme.domain.http.HttpAgency;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/registration-status")
@RegisterRestClient(configKey = "registration-status-api")
public interface RegistrationStatusHttpService {

    @GET
    @Path("/{registrationNumber}")
    HttpAgency findByRegistrationNumber(@PathParam("registrationNumber") String registrationNumber);
}
