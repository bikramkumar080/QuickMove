package com.quickMove.service;

import com.quickMove.dto.DriverDto;
import com.quickMove.dto.UserDTO;
import com.quickMove.dto.VehicleDTO;
import com.quickMove.model.User;
import com.quickMove.model.VehicleType;
import com.quickMove.repository.UserRepository;
import com.quickMove.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    public String getUserRole(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user != null) {
            return user.getRole(); // Assuming User has a getRole() method
        }
        return null;
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(this::convertToUserDTO).collect(Collectors.toList());
    }

    public UserDTO saveUser(User user) {
        User savedUser = userRepository.save(user);
        return convertToUserDTO(savedUser);
    }

    public Optional<UserDTO> findUserById(Long id) {
        return userRepository.findById(id).map(this::convertToUserDTO);
    }
    public Optional<DriverDto> findDriverById(Long id) {
        return userRepository.findById(id).map(this::convertToDriverDto);
    }

    public Optional<UserDTO> updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setEmail(userDetails.getEmail());
            user.setName(userDetails.getName());
            user.setPhone(userDetails.getPhone());
            user.setRole(userDetails.getRole());
            user.setPassword(userDetails.getPassword());
            User updatedUser = userRepository.save(user);
            return convertToUserDTO(updatedUser);
        });
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setOrganizationId(user.getOrganization().getId());
        return dto;
    }
    private DriverDto convertToDriverDto(User user) {
        DriverDto dto = new DriverDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setOrganizationId(user.getOrganization().getId());
        dto.setCapacity(user.getVehicleType().getCapacity());
        dto.setVehicleType(user.getVehicleType().getType());
        dto.setLicenseNumber(user.getLicenseNumber());
        dto.setVehicleNumber(user.getVehicleNumber());
        dto.setVehicleModel(user.getVehicleModel());
        dto.setVehicleColor(user.getVehicleColor());
        dto.setVehicleTypeId(user.getVehicleType().getId());
        return dto;
    }

    public User updateDriverProfile(Long id, VehicleDTO body) {
        User user = userRepository.findUserById(id);
        VehicleType type = vehicleTypeRepository.findByType(body.getVehicleType());
        if (type == null) {
            type = new VehicleType();
            type.setType(body.getVehicleType());
            type.setCapacity(4);
            vehicleTypeRepository.save(type);
        }
        if (user != null) {
            user.setVehicleType(type);
            user.setLicenseNumber(body.getLicenseNumber());
            user.setVehicleModel(body.getVehicleModel());
            user.setVehicleColor(body.getVehicleColor());
            user.setVehicleNumber(body.getVehicleNumber());
            user.setLatitude(body.getLatitude());
            user.setLongitude(body.getLongitude());

            return userRepository.save(user);
        }
        return null;
    }

    public User fetchUserDetails(String header) {
        String userName = jwtService.extractUserName(header.substring(7));
        User user = userRepository.findByName(userName);
        return user;
    }
}