package at.aau.ase.cl.api;

import at.aau.ase.cl.api.interceptor.exceptions.InvalidPasswordException;
import at.aau.ase.cl.api.model.*;
import at.aau.ase.cl.mapper.AddressMapper;
import at.aau.ase.cl.mapper.UserMapper;
import at.aau.ase.cl.service.ResetPasswordService;
import at.aau.ase.cl.service.UserService;
import at.aau.ase.cl.util.JWT_Util;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger log = LoggerFactory.getLogger(UserResource.class);
    @Inject
    UserService service;

    @Inject
    ResetPasswordService resetPasswordService;

    @Inject
    Mailer mailer;

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

    @PUT
    @Path("/{id}")
    public Response updateUserInfo(@PathParam("id") UUID id, @Valid UserPayload userPayload) {
        var updatedUser = service.updateUserInfo(id, userPayload.email, userPayload.username);
        var resultDto = UserMapper.INSTANCE.map(updatedUser);
        return Response.ok(resultDto).build();
    }

    @PUT
    @Path("/{id}/password")
    public Response updatePassword(@PathParam("id") UUID id, @Valid PasswordPayload passwordPayload) {
        var user = service.getUserById(id);

        if (!BCrypt.checkpw(passwordPayload.oldPassword, user.password)) {
            throw new InvalidPasswordException("Invalid old password for user: " + user.username);
        }

        String hashedNewPassword = BCrypt.hashpw(passwordPayload.newPassword, BCrypt.gensalt());
        var updatedUser = service.updatePassword(id, hashedNewPassword);
        var resultDto = UserMapper.INSTANCE.map(updatedUser);
        return Response.ok(resultDto).build();
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

    @POST
    @Path("/forgot-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response forgotPassword(UserEmailPayload payload) {
        String email = payload.email;
        System.out.println(email);
        if (email == null || email.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email cannot be null or empty").build();
        }

        var user = service.findByUsernameOrEmail(email);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        UUID resetToken = UUID.randomUUID();
        resetPasswordService.savePasswordResetToken(user.id, resetToken);

        String resetLink = "http://localhost:8080/user/reset-password?token=" + resetToken;
        mailer.send(Mail.withText(
                email,
                "Password Reset Request",
                "Click the link to reset your Crowd Library Password: " + resetLink
        ));

        return Response.ok("Password reset email sent").build();
    }


    @POST
    @Path("/reset-password")
    public Response resetPassword(@QueryParam("token") String token, @QueryParam("newPassword") String newPassword) {
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Token cannot be null or empty").build();
        }

        var resetTokenEntity = resetPasswordService.getResetPasswordEntityByToken(token);
        if (resetTokenEntity == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid or expired token").build();
        }

        var user = service.getUserById(resetTokenEntity.userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        service.updatePassword(user.id, hashedNewPassword);

        resetPasswordService.invalidateToken(resetTokenEntity);

        return Response.ok("Password reset successfully").build();
    }

}
