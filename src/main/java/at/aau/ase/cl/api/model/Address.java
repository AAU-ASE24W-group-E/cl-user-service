package at.aau.ase.cl.api.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class Address {
    public UUID id;

    @Size(min = 1, max = 255)
    @NotNull
    public String street;

    @Size(min = 1, max = 255)
    @NotNull
    public String city;

    @Size(min = 1, max = 50)
    @NotNull
    public String postalCode;

    @Size(min = 1, max = 255)
    @NotNull
    public String country;

    public Address() {
    }

    public Address(String street, String city, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }
}
