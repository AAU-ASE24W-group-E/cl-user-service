package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.User;
import at.aau.ase.cl.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity map(User user);

    User map(UserEntity userEntity);
}
