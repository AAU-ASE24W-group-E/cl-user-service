package at.aau.ase.cl.api;

import at.aau.ase.cl.api.interceptor.exceptions.InvalidPasswordException;
import at.aau.ase.cl.api.model.*;
import at.aau.ase.cl.mapper.AddressMapper;
import at.aau.ase.cl.mapper.UserMapper;
import at.aau.ase.cl.service.ResetPasswordService;
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
import io.quarkus.logging.Log;

import java.util.UUID;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    UserService service;

    @Inject
    ResetPasswordService resetPasswordService;


    @POST
    @Path("/")
    public Response createUser(User user) {
        var model = UserMapper.INSTANCE.map(user);
        model = service.createUser(model);
        var result = UserMapper.INSTANCE.mapWithoutPassword(model);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))})
    public Response getUser(@PathParam("id") UUID id) {
        var model = service.getUserById(id);
        var result = UserMapper.INSTANCE.mapWithoutPassword(model);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUserInfo(@PathParam("id") UUID id, @Valid UserPayload userPayload) {
        var updatedUser = service.updateUserInfo(id, userPayload.email, userPayload.username);
        var resultDto = UserMapper.INSTANCE.mapWithoutPassword(updatedUser);
        return Response.ok(resultDto).build();
    }

    @PUT
    @Path("/{id}/password")
    public Response updatePassword(@PathParam("id") UUID id, @Valid PasswordPayload passwordPayload) {
        var user = service.getUserById(id);

        if (!JWT_Util.checkPassword(passwordPayload.oldPassword, user.password)) {
            throw new InvalidPasswordException("Invalid old password for user: " + user.username);
        }

        String hashedNewPassword = JWT_Util.hashPassword(passwordPayload.newPassword);
        var updatedUser = service.updatePassword(id, hashedNewPassword);
        var resultDto = UserMapper.INSTANCE.mapWithoutPassword(updatedUser);
        return Response.ok(resultDto).build();
    }

    @POST
    @Path("/{id}/address")
    public Response addAddressToUser(@PathParam("id") UUID id,
                                     @Valid Address address) {
        var modelAddress = AddressMapper.INSTANCE.map(address);
        var modelUser = service.addAddressToUser(id, modelAddress);
        var result = UserMapper.INSTANCE.mapWithoutPassword(modelUser);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/address")
    public Response updateAddress(@PathParam("id") UUID id,
                                  @Valid Address address) {
        var modelAddress = AddressMapper.INSTANCE.map(address);
        var modelUser = service.updateAddress(id, modelAddress);
        var result = UserMapper.INSTANCE.mapWithoutPassword(modelUser);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/set-initial-login")
    public Response updateInitialLoginState(@PathParam("id") UUID id) {
        var updatedUser = service.updateInitialLoginState(id);
        var resultDto = UserMapper.INSTANCE.mapWithoutPassword(updatedUser);

        return Response.ok(resultDto).build();
    }


    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest loginRequest) {
        var user = service.findByUsernameOrEmail(loginRequest.username);

        if (!JWT_Util.checkPassword(loginRequest.password, user.password)) {
            throw new InvalidPasswordException("Invalid password for user: " + user.username);
        }

        String token = JWT_Util.generateToken(user.id.toString(), user.username, user.role);
        User userDto = UserMapper.INSTANCE.mapWithoutPassword(user);
        return Response.ok(new LoginResponse(token, userDto)).build();
    }

    @POST
    @Path("/forgot-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response forgotPassword(UserEmailPayload payload) {
        if (payload.email == null || payload.email.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email cannot be null or empty").build();
        }

        resetPasswordService.initiatePasswordReset(payload.email);
        return Response.ok("Password reset email sent").build();
    }

    @POST
    @Path("/reset-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPassword(ResetPasswordPayload payload) {
        if (payload.token == null || payload.token.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Token cannot be null or empty").build();
        }

        Log.info(payload.token);

        resetPasswordService.resetPassword(payload.token, payload.newPassword);
        return Response.ok("Password reset successfully").build();
    }
}
