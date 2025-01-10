package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.*;
import at.aau.ase.cl.mapper.AddressMapper;
import at.aau.ase.cl.mapper.UserMapper;
import at.aau.ase.cl.service.UserService;
import at.aau.ase.cl.util.JWT_Util;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

@Path("/")
public class UserResource {
    @Inject
    UserService service;

    @POST
    @Path("user")
    public Response createUser(User user) {
        var model = UserMapper.INSTANCE.map(user);
        model = service.createUser(model);
        var result = UserMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }

    @GET
    @Path("user/{id}")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))})
    public Response getUser(@PathParam("id") UUID id) {
        var model = service.getUserById(id);
        var result = UserMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }

    @POST
    @Path("user/{id}/address")
    public Response addAddressToUser(@PathParam("id") UUID id,
                                     @Valid Address address) {
        var modelAddress = AddressMapper.INSTANCE.map(address);
        var modelUser = service.addAddressToUser(id, modelAddress);
        var result = UserMapper.INSTANCE.map(modelUser);
        return Response.ok(result).build();
    }

    @PUT
    @Path("user/{id}/address")
    public Response updateAddress(@PathParam("id") UUID id,
                                  @Valid Address address) {
        var modelAddress = AddressMapper.INSTANCE.map(address);
        var modelUser = service.updateAddress(id, modelAddress);
        var result = UserMapper.INSTANCE.map(modelUser);
        return Response.ok(result).build();
    }

    @PUT
    @Path("user/{id}/set-initial-login")
    public Response updateInitialLoginState(@PathParam("id") UUID id) {
        var user = service.getUserById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        user.initialLoginPending = false;
        var result = UserMapper.INSTANCE.map(user);
        return Response.ok(result).build();
    }


    @POST
    @Path("/user/login")
    public Response login(@Valid LoginRequest loginRequest) {
        var user = service.findByUsernameOrEmail(loginRequest.username);
        if (user == null || !BCrypt.checkpw(loginRequest.password, user.password)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
        }

        String token = JWT_Util.generateToken(user.id.toString(), user.username, user.role);
        User userDto = UserMapper.INSTANCE.map(user);
        return Response.ok(new LoginResponse(token, userDto)).build();
    }
}
