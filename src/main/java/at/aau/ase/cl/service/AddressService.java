package at.aau.ase.cl.service;

import at.aau.ase.cl.model.AddressEntity;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class AddressService {

    @Transactional
    public AddressEntity createAddress(AddressEntity address) {
        // create address
        address.persist();
        return address;
    }

    public AddressEntity getAddressById(UUID id) {
        AddressEntity address = AddressEntity.findById(id);
        if (address == null) {
            Log.debugf("Address with id %s not found", id);
            throw new NotFoundException("Address with id " + id + " not found");
        }
        Log.debugf("User with id %s found: %s", id, address);
        return address;
    }
}
