package com.app.ecom.service;

import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.ProdcutResponse;
import com.app.ecom.model.UserRole;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import com.app.ecom.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public List<ProdcutResponse> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToUserResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProdcutResponse> fetchUser(Long id) {
        return userRepository.findById(id).map(this::mapUserToUserResponse);
    }

    public Optional<ProdcutResponse> updateUser(Long id, UserRequest userRequest) {
        return userRepository.findById(id).map(user -> {
            updateExistingUser(user, userRequest);
            return userRepository.save(user);
        }).map(this::mapUserToUserResponse);
    }

    private static void updateExistingUser(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setAddress(userRequest.getAddress());
    }

    public ProdcutResponse createUser(UserRequest userRequest) {
        return mapUserToUserResponse(userRepository.save(mapUserRequestToUser(userRequest)));
    }

    private User mapUserRequestToUser(UserRequest userRequest) {
        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .role(UserRole.CUSTOMER)
                .address(userRequest.getAddress())
                .build();
    }

    private ProdcutResponse mapUserToUserResponse(User user) {
        return ProdcutResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}
