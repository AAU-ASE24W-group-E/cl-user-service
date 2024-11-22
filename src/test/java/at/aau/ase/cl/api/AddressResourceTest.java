package at.aau.ase.cl.api;

import at.aau.ase.cl.model.AddressEntity;
import at.aau.ase.cl.service.AddressService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class AddressResourceTest {
  @Inject
  AddressService addressService;

  @Test
    @Transactional
    public void testCreateAddress() {
      AddressEntity address = new AddressEntity();

      address.street = "street";
      address.city = "city";
      address.country = "country";
      address.postalCode = "9020";

      AddressEntity createdAddress = addressService.createAddress(address);

      Assertions.assertNotNull(createdAddress);
      assertNotNull(createdAddress.id);
      assertNotNull(createdAddress.city, address.city);
      assertNotNull(createdAddress.country, address.country);
      assertNotNull(createdAddress.street, address.street);
      assertNotNull(createdAddress.postalCode, address.postalCode);
  }

}