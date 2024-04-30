package com.develhope.spring.Rent.Services;

import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.DTO.RentModel;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.Rent.Repositories.RentRepository;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.User.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public RentDTO createRent(Long userId, RentDTO rentDTO) {
        RentEntity rentEntity = new RentModel(rentDTO.getStartDate(), rentDTO.getEndDate(), rentDTO.getDailyCost(), rentDTO.getIsPaid(), rentDTO.getVehicleId()).modelToEntity();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !UserTypes.ADMIN.equals(user.getUserType()) && !UserTypes.SELLER.equals(user.getUserType()))
            return null;

        VehicleEntity vehicleEntity = vehicleRepository.findById(rentEntity.getVehicleId()).orElse(null);
        if (vehicleEntity == null)
            return null;

        RentEntity savedRent = rentRepository.save(rentEntity);
        return new RentModel(savedRent.getStartDate(), savedRent.getEndDate(), savedRent.getDailyCost(), savedRent.getIsPaid(), savedRent.getVehicleId()).modelToDTO();
    }

    public List<RentDTO> getRentsByUserId(Long userId) {
        return rentRepository.findByUserId(userId).stream()
                .map(rentEntity -> new RentModel(rentEntity.getStartDate(), rentEntity.getEndDate(), rentEntity.getDailyCost(), rentEntity.getIsPaid(), rentEntity.getVehicleId()).modelToDTO())
                .collect(Collectors.toList());
    }

    public RentDTO getRentById(Long userId, Long rentId) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);
        if (rentEntity == null || !rentEntity.getUser().getId().equals(userId))
            return null;

        return new RentModel(rentEntity.getStartDate(), rentEntity.getEndDate(), rentEntity.getDailyCost(), rentEntity.getIsPaid(), rentEntity.getVehicleId()).modelToDTO();
    }

    public RentDTO updateRentDates(Long userId, Long rentId, RentDTO RentDTO) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);
        if (rentEntity == null || !rentEntity.getUser().getId().equals(userId))
            return null;

        rentEntity.setStartDate(RentDTO.getStartDate());
        rentEntity.setEndDate(RentDTO.getEndDate());
        rentEntity.setTotalCost(rentEntity.calculateTotalCost());

        RentEntity updatedRent = rentRepository.save(rentEntity);
        return new RentModel(updatedRent.getStartDate(), updatedRent.getEndDate(), updatedRent.getDailyCost(), updatedRent.getIsPaid(), updatedRent.getVehicleId()).modelToDTO();
    }

    public boolean deleteRent(Long userId, Long rentId) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);
        if (rentEntity == null || !rentEntity.getUser().getId().equals(userId))
            return false;

        rentRepository.delete(rentEntity);
        return true;
    }
}