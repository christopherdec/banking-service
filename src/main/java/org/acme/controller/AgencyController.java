package org.acme.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import org.acme.domain.Agency;
import org.acme.exception.AgencyNotFoundException;
import org.acme.exception.InactiveAgencyException;
import org.acme.service.AgencyService;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/agency")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Void> register(Agency agency, @Context UriInfo uriInfo) {
        try {
            agencyService.register(agency);
            return RestResponse.created(
                uriInfo.getAbsolutePathBuilder().path(agency.getId().toString()).build()
            );
        } catch (AgencyNotFoundException | InactiveAgencyException e) {
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }
    }

    @GET
    @Path("/{id}")
    public RestResponse<Agency> get(@PathParam("id") Long id) {
        return agencyService.findById(id)
                .map(RestResponse::ok)
                .orElse(RestResponse.notFound());
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<Void> delete(@PathParam("id") Long id) {
        agencyService.deleteById(id);
        return RestResponse.ok();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public RestResponse<Void> update(Agency agency) {
        try {
            agencyService.update(agency);
            return RestResponse.ok();
        } catch (AgencyNotFoundException | InactiveAgencyException e) {
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }
    }
}
