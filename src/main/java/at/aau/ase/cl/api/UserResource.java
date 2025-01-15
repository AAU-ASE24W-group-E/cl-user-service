package at.aau.ase.cl.api;

import at.aau.ase.cl.api.interceptor.InvalidPasswordException;
import at.aau.ase.cl.api.model.*;
import at.aau.ase.cl.mapper.AddressMapper;
import at.aau.ase.cl.mapper.UserMapper;
import at.aau.ase.cl.service.UserService;
import at.aau.ase.cl.util.JWT_Util;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    UserService service;

    @POST
    @Path("/")
    public Response createUser(User user) {
        var model = UserMapper.INSTANCE.map(user);
        model = service.createUser(model);
        var result = UserMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))})
    public Response getUser(@PathParam("id") UUID id) {
        var model = service.getUserById(id);
        var result = UserMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }

    @POST
    @Path("/{id}/address")
    public Response addAddressToUser(@PathParam("id") UUID id,
                                     @Valid Address address) {
        var modelAddress = AddressMapper.INSTANCE.map(address);
        var modelUser = service.addAddressToUser(id, modelAddress);
        var result = UserMapper.INSTANCE.map(modelUser);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/address")
    public Response updateAddress(@PathParam("id") UUID id,
                                  @Valid Address address) {
        var modelAddress = AddressMapper.INSTANCE.map(address);
        var modelUser = service.updateAddress(id, modelAddress);
        var result = UserMapper.INSTANCE.map(modelUser);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/set-initial-login")
    public Response updateInitialLoginState(@PathParam("id") UUID id) {
        var updatedUser = service.updateInitialLoginState(id);
        var resultDto = UserMapper.INSTANCE.map(updatedUser);

        return Response.ok(resultDto).build();
    }


    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest loginRequest) {
        var user = service.findByUsernameOrEmail(loginRequest.username);

        if (!BCrypt.checkpw(loginRequest.password, user.password)) {
            throw new InvalidPasswordException("Invalid password for user: " + user.username);
        }

        String token = JWT_Util.generateToken(user.id.toString(), user.username, user.role);
        User userDto = UserMapper.INSTANCE.map(user);
        return Response.ok(new LoginResponse(token, userDto)).build();
    }
}
