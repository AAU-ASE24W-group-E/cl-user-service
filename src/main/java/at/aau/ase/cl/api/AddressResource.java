package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Address;
import at.aau.ase.cl.api.model.User;
import at.aau.ase.cl.mapper.AddressMapper;
import at.aau.ase.cl.mapper.UserMapper;
import at.aau.ase.cl.service.AddressService;
import at.aau.ase.cl.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.UUID;

@Path("/")
public class AddressResource {
    @Inject
    AddressService service;

    @POST
    @Path("address")
    public Response createAddress(Address address) {
        // create address
        System.out.println("Request angekommen");
        var model = AddressMapper.INSTANCE.map(address);
        System.out.println("model gemappt");
        model = service.createAddress(model);
        System.out.println("addresse erstellt");
        var result = AddressMapper.INSTANCE.map(model);
        System.out.println("result gemappt");
        return Response.ok(result).build();
    }

    @GET
    @Path("address/{id}")
    @APIResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = Address.class))})
    public Response getAddress(@PathParam("id") UUID id) {
        var model = service.getAddressById(id);
        var result = AddressMapper.INSTANCE.map(model);
        return Response.ok(result).build();
    }
}
