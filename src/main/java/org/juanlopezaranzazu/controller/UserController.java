package org.juanlopezaranzazu.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.juanlopezaranzazu.dto.CreateUserDTO;
import org.juanlopezaranzazu.dto.UpdateUserDTO;
import org.juanlopezaranzazu.dto.UserDTO;
import org.juanlopezaranzazu.service.UserService;

import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserService userService;

    @GET
    public Response getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") String id) {
        UserDTO user = userService.getUserById(id);
        return Response.ok(user).build();
    }

    @GET
    @Path("/username/{username}")
    public Response getUserByUsername(@PathParam("username") String username) {
        UserDTO user = userService.getUserByUsername(username);
        return Response.ok(user).build();
    }

    @POST
    public Response createUser(@Valid CreateUserDTO createUserDTO) {
        UserDTO newUser = userService.createUser(createUserDTO);
        return Response.status(Response.Status.CREATED).entity(newUser).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") String id, @Valid UpdateUserDTO updateUserDTO) {
        UserDTO updatedUser = userService.updateUser(id, updateUserDTO);
        return Response.ok(updatedUser).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") String id) {
        userService.deleteUser(id);
        return Response.ok(new MessageResponse("User deleted successfully")).build();
    }


    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }
}