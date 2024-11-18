package at.aau.ase.cl.api.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class User {
    public UUID id;

    @Size(min = 1, max = 255)
    @NotNull
    public String firstName;

    @Size(min = 1, max = 255)
    @NotNull
    public String lastName;

    public Address address;

    public User() {
    }

    public User( String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User( String firstName, String lastName, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }
}
