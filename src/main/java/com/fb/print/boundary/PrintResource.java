package com.fb.print.boundary;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fb.exception.control.ExceptionHandler;
import com.fb.print.control.PrintBA;

/**
 * Rest Resource for Print
 */
@ExceptionHandler
@Path("/print")
public class PrintResource {

    @Inject
    PrintBA printBA;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response print() {
        return printBA.printDocument();
    }
}
