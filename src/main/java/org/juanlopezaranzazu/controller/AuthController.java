package org.juanlopezaranzazu.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.juanlopezaranzazu.dto.CreateUserDTO;
import org.juanlopezaranzazu.dto.LoginDTO;
import org.juanlopezaranzazu.dto.RefreshTokenDTO;
import org.juanlopezaranzazu.dto.TokenDTO;
import org.juanlopezaranzazu.dto.UserDTO;
import org.juanlopezaranzazu.service.AuthService;
import org.juanlopezaranzazu.service.UserService;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/register")
    @PermitAll
    public Response register(@Valid CreateUserDTO createUserDTO) {
        UserDTO userDTO = authService.register(createUserDTO);
        return Response.status(Response.Status.CREATED).entity(userDTO).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginDTO loginDTO) {
        TokenDTO tokenDTO = authService.login(loginDTO);
        return Response.ok(tokenDTO).build();
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public Response refresh(@Valid RefreshTokenDTO refreshTokenDTO) {
        TokenDTO tokenDTO = authService.refreshToken(refreshTokenDTO);
        return Response.ok(tokenDTO).build();
    }

    @POST
    @Path("/logout")
    @RolesAllowed({"USER", "ADMIN"})
    public Response logout() {
        String userId = jwt.getSubject();
        authService.logout(userId);
        return Response.ok().entity(new MessageResponse("Logged out successfully")).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({"USER", "ADMIN"})
    public Response getCurrentUser() {
        String userId = jwt.getSubject();
        UserDTO userDTO = userService.getUserById(userId);
        return Response.ok(userDTO).build();
    }

    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }
}