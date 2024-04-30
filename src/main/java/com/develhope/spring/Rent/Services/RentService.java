package com.develhope.spring.Rent.Services;

import com.develhope.spring.Rent.Entities.DTO.RentDTO;
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

@Service
public class RentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public RentDTO createRent(Long userId, RentDTO rentDTO) {
        RentEntity rentEntity = rentDTO.dtoToModel().toEntity();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !UserTypes.ADMIN.equals(user.getUserType()) && !UserTypes.SELLER.equals(user.getUserType()))
            return null;

        VehicleEntity vehicleEntity = vehicleRepository.findById(rentEntity.getVehicleId()).orElse(null);
        if (vehicleEntity == null)
            return null;

        return rentRepository.save(rentEntity).toModel().modelToDTO();
    }

    public List<RentDTO> getRentsByUserId(Long userId) {
        return rentRepository.findByUserId(userId).stream().map(rentEntity -> {
            return rentEntity.toModel().modelToDTO();
        }).toList();
    }

    public RentDTO getRentById(Long userId, Long rentId) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);
        if (rentEntity == null || !rentEntity.getUser().getId().equals(userId))
            return null;

        return rentEntity.toModel().modelToDTO();
    }

    public RentDTO updateRentDates(Long userId, Long rentId, RentDTO RentDTO) {
        RentEntity rentEntity = getRentById(userId, rentId).dtoToModel().toEntity();
        if (rentEntity == null)
            return null;

        rentEntity.setStartDate(RentDTO.getStartDate());
        rentEntity.setEndDate(RentDTO.getEndDate());
        rentEntity.setTotalCost(rentEntity.calculateTotalCost());

        return rentRepository.save(rentEntity).toModel().modelToDTO();
    }

    public boolean deleteRent(Long userId, Long rentId) {
        RentEntity rentEntity = getRentById(userId, rentId).dtoToModel().toEntity();
        if (rentEntity == null)
            return false;

        rentRepository.delete(rentEntity);
        return true;
    }
}