package com.develhope.spring.User.Services;


import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.User.UserDTO.UserDTO;
import com.develhope.spring.User.UserDTO.UserModel;
import com.develhope.spring.order.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    public UserDTO updateUser(long userId, UserModel updateUserRequest) {
        Optional<User> result = userRepository.findById(userId);

        if (result.isPresent()) {
            result.get().setName(updateUserRequest.getName() == null ? result.get().getName() : updateUserRequest.getName());
            result.get().setSurname(updateUserRequest.getSurname() == null ? result.get().getSurname() : updateUserRequest.getSurname());
            result.get().setEmail(updateUserRequest.getEmail());
            result.get().setPhoneNumber(updateUserRequest.getPhoneNumber() == null ? result.get().getPhoneNumber() : updateUserRequest.getPhoneNumber());
            result.get().setUserType(updateUserRequest.getUserType());
            result.get().setPassword(updateUserRequest.getPassword());
            User saveUser = userRepository.saveAndFlush(result.get());
            UserModel savedUserModel = UserModel.entityToModel(saveUser);
            return UserModel.modelToDto(savedUserModel);
        } else {
            return null;
        }
    }
    public UserDTO getSingleUser(long userId) {
        Optional<User> result = userRepository.findById(userId);
        if (result.isPresent()) {
            UserModel userModel = UserModel.entityToModel(result.get());
            return UserModel.modelToDto(userModel);
        } else {
            return null;
        }
    }
    public UserDTO createUser(UserDTO createUserRequest) {
        try {
            UserModel userModel = new UserModel(createUserRequest.getId(), createUserRequest.getName(), createUserRequest.getSurname(), createUserRequest.getEmail(), createUserRequest.getPhoneNumber(), createUserRequest.getPassword());
            UserModel userModel1 = UserModel.entityToModel(userRepository.save(UserModel.modelToEntity(userModel)));
            return UserModel.modelToDto(userModel1);
        } catch (Exception e) {
            return null;
        }
    }
    public boolean deleteUser(long userId) {
        Optional<User> result = userRepository.findById(userId);
        if (result.isPresent()) {
            try {
                userRepository.delete(result.get());
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

}
