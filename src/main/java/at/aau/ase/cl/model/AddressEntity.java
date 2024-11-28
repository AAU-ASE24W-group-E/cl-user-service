package at.aau.ase.cl.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Embeddable
public class AddressEntity extends PanacheEntityBase {
    @NotNull
    public String street;

    @NotNull
    public String city;

    @NotNull
    public String postalCode;

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}$", message = "Invalid Country Code")
    public String countryCode;

}
