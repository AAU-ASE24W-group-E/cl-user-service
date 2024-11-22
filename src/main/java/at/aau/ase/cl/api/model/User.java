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

    @Size(min = 1, max = 255)
    @NotNull
    public String email;

    @Size(min = 1, max = 255)
    @NotNull
    public String username;

    public Address address;

    public User() {
    }

    public User( String firstName, String lastName, String email, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
    }

    public User( String firstName, String lastName,String email, String username, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.address = address;
    }
}
