package at.aau.ase.cl.api.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class User {
    public UUID id;

    @Size(min = 1, max = 255)
    @NotNull
    public String email;

    @Size(min = 1, max = 255)
    @NotNull
    public String username;

    public Address address;

    @Size(min = 8, max = 255)
    @NotNull
    public String password;

    public User() {
    }

    public User( String email, String username, Address address, String password) {
        this.email = email;
        this.username = username;
        this.address = address;
        this.password = password;
    }
}
