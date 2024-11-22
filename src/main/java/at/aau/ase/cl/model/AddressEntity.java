package at.aau.ase.cl.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "address")
@Getter @Setter
public class AddressEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
