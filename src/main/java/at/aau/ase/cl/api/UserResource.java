package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Address;
import at.aau.ase.cl.api.model.User;
import at.aau.ase.cl.mapper.AddressMapper;
import at.aau.ase.cl.mapper.UserMapper;
import at.aau.ase.cl.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.UUID;

@Path("/")
public class UserResource {
    @Inject
    UserService service;

    @POST
    @Path("user")
    public Response createUser(User user) {
        // create user
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
    public Response addAddressToUser(@PathParam("id") UUID id, @Valid Address address) {
        var modelAddress = AddressMapper.INSTANCE.map(address);

        try{
            var modelUser = service.addAddressToUser(id, modelAddress);
            var result = UserMapper.INSTANCE.map(modelUser);
            return Response.ok(result).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("User with ID " + id + " not found").build();
        }
    }
}
