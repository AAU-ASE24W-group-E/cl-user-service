package at.aau.ase.cl.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Embeddable
public class AddressEntity extends PanacheEntityBase {
    @NotNull
    public String street;

    @NotNull
    public String city;

    @NotNull
    public String postalCode;

    @NotNull
    public String country;

}
