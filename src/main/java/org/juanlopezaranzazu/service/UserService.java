package org.juanlopezaranzazu.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.juanlopezaranzazu.dto.UpdateUserDTO;
import org.juanlopezaranzazu.dto.UserDTO;
import org.juanlopezaranzazu.entity.User;
import org.juanlopezaranzazu.exception.BadRequestException;
import org.juanlopezaranzazu.exception.ResourceNotFoundException;
import org.juanlopezaranzazu.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.listAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(String id) {
        User user = userRepository.findByIdOptional(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToDTO(user);
    }

    public UserDTO updateUser(String id, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findByIdOptional(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().equals(user.email)) {
            if (userRepository.existsByEmail(updateUserDTO.getEmail())) {
                throw new BadRequestException("Email is already in use");
            }
            user.email = updateUserDTO.getEmail();
        }

        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            user.password = BCrypt.hashpw(updateUserDTO.getPassword(), BCrypt.gensalt());
        }

        if (updateUserDTO.getFirstName() != null) {
            user.firstName = updateUserDTO.getFirstName();
        }

        if (updateUserDTO.getLastName() != null) {
            user.lastName = updateUserDTO.getLastName();
        }

        user.updatedAt = LocalDateTime.now();
        userRepository.update(user);

        return mapToDTO(user);
    }

    public void deleteUser(String id) {
        User user = userRepository.findByIdOptional(new ObjectId(id))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.id.toString());
        dto.setUsername(user.username);
        dto.setEmail(user.email);
        dto.setFirstName(user.firstName);
        dto.setLastName(user.lastName);
        dto.setRoles(user.roles);
        dto.setCreatedAt(user.createdAt);
        dto.setUpdatedAt(user.updatedAt);
        return dto;
    }
}