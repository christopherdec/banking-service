package org.acme.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgencyMetrics {

    NOT_FOUND_COUNTER("agency_not_found_counter"),
    INACTIVE_COUNTER("agency_inactive_counter"),
    ADDED_COUNTER("agency_added_counter"),
    DELETED_COUNTER("agency_deleted_counter"),
    DELETE_FAILED_COUNTER("agency_delete_failed_counter"),
    UPDATED_COUNTER("agency_updated_counter");

    private final String value;

}