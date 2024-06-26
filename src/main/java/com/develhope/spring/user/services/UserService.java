package com.develhope.spring.user.services;

import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.DTO.UserDTO;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.model.UserModel;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.user.request.UserRequest;
import com.develhope.spring.user.response.UserResponse;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public Either<UserResponse, UserDTO> getSingleUser(UserEntity userEntity, Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new UserResponse(404, "user with id" + userEntity.getId() + "not found"));
        }
        UserModel userModel = UserModel.entityToModel(userOptional.get());
        return Either.right(UserModel.modelToDto(userModel));
    }

    public Either<UserResponse, List<UserDTO>> findByUserType(String userType) {
        List<UserEntity> userEntities = userRepository.findByUserType(UserTypes.convertFromString(userType));
        if (userEntities.isEmpty()) {
            return Either.left(new UserResponse(404, "No users found"));
        }
        List<UserDTO> userDTOS = userEntities.stream()
                .map(UserModel::entityToModel)
                .map(UserModel::modelToDto)
                .collect(Collectors.toList());

        return Either.right(userDTOS);
    }

    public Either<UserResponse, UserDTO> findByNameAndSurname(String name, String surname) {
        Optional<UserEntity> userOptional = userRepository.findByNameAndSurname(name, surname);
        if (userOptional.isEmpty()) {
            return Either.left(new UserResponse(404, "No user found"));
        }
        UserModel userModel = UserModel.entityToModel(userOptional.get());
        return Either.right(UserModel.modelToDto(userModel));
    }

    public Either<UserResponse, UserDTO> updateUser(UserEntity userEntity, Long userId, UserRequest request) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        if (userOptional.isEmpty()) {
            return Either.left(new UserResponse(404, "No user found"));
        }

        userOptional.get().setName(request.getName() == null ? userOptional.get().getName() : request.getName());
        userOptional.get().setSurname(request.getSurname() == null ? userOptional.get().getSurname() : request.getSurname());
        userOptional.get().setPhoneNumber(request.getPhoneNumber() == null ? userOptional.get().getPhoneNumber() : request.getPhoneNumber());
        userOptional.get().setEmail(request.getEmail() == null ? userOptional.get().getEmail() : request.getEmail());
        userOptional.get().setUserType(request.getUserType() == null ? userOptional.get().getUserType() : request.getUserType());

        UserEntity savedEntity = userRepository.saveAndFlush(userOptional.get());
        UserModel myUserModel = UserModel.entityToModel(savedEntity);
        return Either.right(UserModel.modelToDto(myUserModel));

    }

    public UserResponse deleteUser(UserEntity userEntity, Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return new UserResponse(403, "this user does not have the permission");
        }
        try {
            userRepository.delete(userOptional.get());
            return new UserResponse(200, "User deleted successfully");
        } catch (Exception e) {
            return new UserResponse(500, "Internal server error");
        }
    }

}
