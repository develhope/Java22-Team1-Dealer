package com.develhope.spring.Rent.Services;

import com.develhope.spring.Rent.Entities.DTO.ModifyRentDTO;
import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.Rent;
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

    public Rent createRent(Long userId, RentDTO rentDTO) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || !UserTypes.ADMIN.equals(user.getUserType()) && !UserTypes.SELLER.equals(user.getUserType()))
            return null;

        VehicleEntity vehicleEntity = vehicleRepository.findById(rentDTO.getVehicleId()).orElse(null);
        if (vehicleEntity == null)
            return null;

        Rent rent = new Rent();
        rent.setUser(user);
        rent.setVehicle(vehicleEntity);
        rent.setStartDate(rentDTO.getStartDate());
        rent.setEndDate(rentDTO.getEndDate());
        rent.setDailyCost(rentDTO.getDailyCost());
        rent.setIsPaid(rentDTO.getIsPaid());
        rent.setTotalCost(rent.calculateTotalCost());

        return rentRepository.save(rent);
    }

    public List<Rent> getRentsByUserId(Long userId) {
        return rentRepository.findByUserId(userId);
    }

    public Rent getRentById(Long userId, Long rentId) {
        Rent rent = rentRepository.findById(rentId).orElse(null);
        if (rent == null || !rent.getUser().getId().equals(userId))
            return null;

        return rent;
    }

    public Rent updateRentDates(Long userId, Long rentId, ModifyRentDTO modifyRentDTO) {
        Rent rent = getRentById(userId, rentId);
        if (rent == null)
            return null;

        rent.setStartDate(modifyRentDTO.getStartDate());
        rent.setEndDate(modifyRentDTO.getEndDate());
        rent.setTotalCost(rent.calculateTotalCost());

        return rentRepository.save(rent);
    }

    public boolean deleteRent(Long userId, Long rentId) {
        Rent rent = getRentById(userId, rentId);
        if (rent == null)
            return false;

        rentRepository.delete(rent);
        return true;
    }
}