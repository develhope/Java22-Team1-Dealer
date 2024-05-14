package com.develhope.spring.User.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserDTO;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Entities.UserModel;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.User.Response.UserResponse;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
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

    public Either<UserResponse, List<UserDTO>> findByUserType(UserTypes userType) {
        List<UserEntity> userEntities = userRepository.findByUserType(userType);
        if (userEntities.isEmpty()) {
            return Either.left(new UserResponse(404, "No users found"));
        }
        List<UserDTO> userDTOS = userEntities.stream()
                .map(UserModel::entityToModel)
                .map(UserModel::modelToDto)
                .collect(Collectors.toList());

        return Either.right(userDTOS);
    }
}
