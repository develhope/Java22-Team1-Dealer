package com.develhope.spring;

import com.develhope.spring.User.entities.Enum.UserTypes;
import com.develhope.spring.User.entities.UserEntity;
import com.develhope.spring.User.repositories.UserRepository;
import com.develhope.spring.vehicles.DTO.VehicleDTO;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import com.develhope.spring.vehicles.request.VehicleRequest;
import com.develhope.spring.vehicles.response.VehicleResponse;
import com.develhope.spring.vehicles.services.VehicleCRUDService;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VehicleCRUDServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VehicleCRUDService vehicleCRUDService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Verifica che un veicolo possa essere creato con successo se l'utente è un amministratore.
    @Test
    void testCreateVehicle_UserIsAdmin_Success() {
        UserEntity adminUser = new UserEntity();
        adminUser.setId(1L);
        adminUser.setUserType(UserTypes.ADMIN);

        VehicleRequest vehicleRequest = new VehicleRequest();
        vehicleRequest.setBrand("Toyota");
        vehicleRequest.setModel("Corolla");
        vehicleRequest.setDisplacement((int) 1.8);
        vehicleRequest.setColor("Blue");
        vehicleRequest.setPower(140);
        vehicleRequest.setTransmission("Automatic");
        vehicleRequest.setRegistrationYear(2021);
        vehicleRequest.setPowerSupply("Gasoline");
        vehicleRequest.setPrice(BigDecimal.valueOf(20000));
        vehicleRequest.setDiscount(BigDecimal.valueOf(5));
        vehicleRequest.setAccessories(Collections.singletonList("Air Conditioning"));
        vehicleRequest.setIsNew(true);
        vehicleRequest.setVehicleStatus("purchasable");
        vehicleRequest.setVehicleType("car");

        VehicleEntity savedVehicle = new VehicleEntity();
        savedVehicle.setVehicleId(1L);

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(savedVehicle);

        Either<VehicleResponse, VehicleDTO> result = vehicleCRUDService.createVehicle(adminUser, vehicleRequest);

        assertTrue(result.isRight());
        verify(userRepository, times(1)).findById(adminUser.getId());
        verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
    }

    // Verifica che la creazione del veicolo fallisca se l'utente non è un amministratore.
    @Test
    void testCreateVehicle_UserIsNotAdmin_Failure() {
        UserEntity regularUser = new UserEntity();
        regularUser.setId(2L);
        regularUser.setUserType(UserTypes.BUYER);

        VehicleRequest vehicleRequest = new VehicleRequest();

        when(userRepository.findById(regularUser.getId())).thenReturn(Optional.of(regularUser));

        Either<VehicleResponse, VehicleDTO> result = vehicleCRUDService.createVehicle(regularUser, vehicleRequest);

        assertTrue(result.isLeft());
        assertEquals(400, result.getLeft().getCode());
        verify(userRepository, times(1)).findById(regularUser.getId());
        verify(vehicleRepository, times(0)).save(any(VehicleEntity.class));
    }

    // Verifica che la creazione del veicolo fallisca se l'utente non viene trovato nel repository.
    @Test
    void testCreateVehicle_UserNotFound_Failure() {
        UserEntity user = new UserEntity();
        user.setId(3L);

        VehicleRequest vehicleRequest = new VehicleRequest();

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Either<VehicleResponse, VehicleDTO> result = vehicleCRUDService.createVehicle(user, vehicleRequest);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(vehicleRepository, times(0)).save(any(VehicleEntity.class));
    }
}