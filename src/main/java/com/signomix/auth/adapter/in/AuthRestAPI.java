package com.signomix.auth.adapter.in;

import org.jboss.logging.Logger;

import com.signomix.auth.application.in.AuthPort;
import com.signomix.common.User;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/api/authenticate")
public class AuthRestAPI {

    @Inject
    Logger logger;

    @Inject
    AuthPort authPort;

    @GET
    public Response getSessionToken(@QueryParam("login") String login, @QueryParam("password") String password) {
        String token = authPort.getSessionToken(login, password);
        return Response.ok().build();
    }

    @GET
    @Path("/{token}")
    public Response getUser(@PathParam("token") String token) {
        User user=authPort.getUser(token);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{token}")
    public Response removeSession(@PathParam("token") String token) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        authPort.removeSession(token);
        return Response.ok().build();
    }

}
