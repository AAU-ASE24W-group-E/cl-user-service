package at.aau.ase.cl.api.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Arrays;
import java.util.Locale;
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

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}$", message = "Invalid Country Code")
    public String countryCode;

    public Address() {
    }

    public Address(String street, String city, String postalCode, String countryCode) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        validateCountriCode(this.countryCode);
    }

    public void validateCountriCode(String countryCode) {
        if (Arrays.stream(Locale.getISOCountries()).noneMatch(loc -> loc.equalsIgnoreCase(countryCode))) {
            throw new IllegalArgumentException("Invalid Country Code");
        }
    }
}
