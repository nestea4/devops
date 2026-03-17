package com.example.userservice.mappers;/*
    @author User
    @project lab4
    @class UserMapper
    @version 1.0.0
    @since 28.04.2025 - 15.23 
*/

import com.example.userservice.DTO.UserCreateDTO;
import com.example.userservice.DTO.UserDTO;
import com.example.userservice.DTO.UserResponseDTO;
import com.example.userservice.DTO.UserUpdateDTO;
import com.example.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUser(UserDTO userDTO);

    // @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateDTO userCreateDTO);

    @Mappings({
            @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    })
    UserResponseDTO toUserResponse(User user);

    List<UserResponseDTO> toUserResponseList(List<User> users);

    void updateUserFromDto(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
}
