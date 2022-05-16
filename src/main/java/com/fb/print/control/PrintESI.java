package com.fb.print.control;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

/**
 * External Service Interface for Print
 */
@ApplicationScoped
public class PrintESI {

    @Inject
    @RestClient
    PrintRestCaller restCaller;

    /**
     * Request document print
     *
     * @param guid   template id
     * @param body   multipart data
     * @param client client calling cib
     * @param role   role
     * @return printed document
     */
    public Response printDocument(String guid, MultipartFormDataOutput body, String client, String role) {
        return restCaller.printDocument(guid, body, client, role);
    }
}
