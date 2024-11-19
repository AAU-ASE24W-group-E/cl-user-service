package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.Address;
import at.aau.ase.cl.api.model.AddressEntity;
import at.aau.ase.cl.api.model.User;
import at.aau.ase.cl.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressEntity map(Address address);

    Address map(AddressEntity addressEntity);
}
