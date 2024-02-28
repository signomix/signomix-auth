package com.signomix.auth.adapter.in;

import java.util.Base64;

import org.jboss.logging.Logger;

import com.signomix.auth.application.in.AuthPort;
import com.signomix.common.Token;
import com.signomix.common.User;

import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
public class AuthRestAPI {

    @Inject
    Logger logger;

    @Inject
    AuthPort authPort;

    @Inject
    RoutingContext context;

    /**
     * Get session token for user
     * @param login
     * @param password
     * @return
     */
    @POST
    @Path("/v2")
    public Response startSession(@HeaderParam("Authentication") String authHeader) {
        String remoteAddress = context.request().remoteAddress().hostAddress();
        if (authHeader == null || authHeader.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String[] parts = authHeader.split(" ");
        if (parts.length != 2) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String[] credentials = new String(Base64.getDecoder().decode(parts[1])).split(":");
        if (credentials.length != 2) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String login=credentials[0];
        String password=credentials[1];
        logger.info("startSession: "+login+" "+password);
        String token = authPort.getSessionToken(login, password, remoteAddress);
        logger.info("startSession: "+token);
        return Response.ok().entity(token).build();
    }

/*     @GET
    public Response getSessionTokenForUser(@HeaderParam("token") String token, @QueryParam("uid") String uid, @QueryParam("permanent") boolean permanent) {
        User user=authPort.getUser(token);
        String newToken = authPort.getTokenForUser(user, uid, permanent);
        return Response.ok().entity(newToken).build();
    } */

    @GET
    @Path("/v2/{token}")
    public Response getUser(@PathParam("token") String token) {
        User user=authPort.getUser(token);
        return Response.ok().entity(user).build();
    }

    @DELETE
    @Path("/v2/{token}")
    public Response removeSession(@PathParam("token") String token) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        authPort.removeSession(token);
        return Response.ok().build();
    }

    @GET
    @Path("/token/{token}")
    public Response getToken(@PathParam("token") String tokenId){
        logger.info("getToken: "+tokenId);
        if (tokenId == null || tokenId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Token token = authPort.findToken(tokenId);
        if(token==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(token).build();
    }

/*     @POST
    @Path("/register")
    public Response registerUser(@QueryParam("login") String login, @QueryParam("password") String password) {
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        authPort.registerUser(login, password);
        return Response.ok().build();
    }

    @POST
    @Path("/resetpassword")
    public Response resetPassword(@QueryParam("login") String login) {
        if (login == null || login.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        authPort.resetPassword(login);
        return Response.ok().build();
    } */

}
