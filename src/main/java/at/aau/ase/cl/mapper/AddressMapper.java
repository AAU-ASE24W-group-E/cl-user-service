package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.Address;
import at.aau.ase.cl.model.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressEntity map(Address address);

    Address map(AddressEntity addressEntity);
}
