package at.aau.ase.cl.api.model;

import jakarta.validation.constraints.*;

import java.util.Arrays;
import java.util.Locale;

public class Address {
    @NotNull
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    public Double latitude;

    @NotNull
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    public Double longitude;

    public Address() {
    }

    public Address(Double latitude,
                   Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void validateCountryCode(String countryCode) {
        if (Arrays.stream(Locale.getISOCountries()).noneMatch(loc -> loc.equalsIgnoreCase(countryCode))) {
            throw new IllegalArgumentException("Invalid Country Code");
        }
    }
}
