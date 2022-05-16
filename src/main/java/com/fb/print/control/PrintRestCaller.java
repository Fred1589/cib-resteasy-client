package com.fb.print.control;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import com.fb.BaseResponseExceptionMapper;
import com.fb.exception.entity.CibClientException;
import com.fb.exception.entity.ErrorOrigin;

/**
 * RestCaller for Print endpoints
 */
@RegisterRestClient(configKey = "print-api")
@RegisterProvider(value = PrintRestCaller.PrintResponseExceptionMapper.class)
public interface PrintRestCaller {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/generate/pdf/{guid}")
    Response printDocument(@PathParam("guid") String guid, MultipartFormDataOutput body,
            @QueryParam("client") String client, @QueryParam("role") String role);

    /**
     * Automatic mapping for external errors to {@link CibClientException}
     */
    class PrintResponseExceptionMapper extends BaseResponseExceptionMapper {
        @Override
        protected ErrorOrigin getErrorOrigin() {
            return ErrorOrigin.CIB_CLIENT;
        }
    }
}
