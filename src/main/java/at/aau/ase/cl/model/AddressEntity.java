package at.aau.ase.cl.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "address")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public UUID id;

    @NotNull
    @Column(name = "street", nullable = false)
    public String street;

    @NotNull
    @Column(name = "city", nullable = false)
    public String city;

    @NotNull
    @Column(name = "postal_code", nullable = false)
    public String postalCode;

    @NotNull
    @Column(name = "country", nullable = false)
    public String country;
}
