package com.develhope.spring;

import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.DTO.VehicleDTO;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.model.VehicleModel;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import com.develhope.spring.vehicles.request.VehicleRequest;
import com.develhope.spring.vehicles.response.VehicleErrorResponse;
import com.develhope.spring.vehicles.services.VehicleCRUDService;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @Spy
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
        vehicleRequest.setBrand("Lamborghini");
        vehicleRequest.setModel("Revuelto");
        vehicleRequest.setDisplacement((int) 6.4);
        vehicleRequest.setColor("Black");
        vehicleRequest.setPower(1015);
        vehicleRequest.setTransmission("Automatic");
        vehicleRequest.setRegistrationYear(2021);
        vehicleRequest.setPowerSupply("PHEV / Gasoline");
        vehicleRequest.setPrice(BigDecimal.valueOf(517255));
        vehicleRequest.setDiscount(BigDecimal.valueOf(1));
        vehicleRequest.setAccessories(Collections.singletonList("Air Conditioning"));
        vehicleRequest.setIsNew(true);
        vehicleRequest.setVehicleStatus("purchasable");
        vehicleRequest.setVehicleType("car");

        VehicleEntity savedVehicle = new VehicleEntity();
        savedVehicle.setVehicleId(1L);

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(savedVehicle);

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.createVehicle(adminUser, vehicleRequest);

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

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.createVehicle(regularUser, vehicleRequest);

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

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.createVehicle(user, vehicleRequest);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(vehicleRepository, times(0)).save(any(VehicleEntity.class));
    }

    // Verifica che il metodo ritorni un errore 404 se l'utente non viene trovato.
    @Test
    void testGetSingleVehicle_UserNotFound() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.getSingleVehicle(user, 1L);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(vehicleRepository, times(0)).findById(anyLong());
    }

    // Verifica che il metodo ritorni un errore 400 se l'utente non è un compratore o un venditore.
    @Test
    void testGetSingleVehicle_UserNotBuyerOrSeller() {
        UserEntity user = new UserEntity();
        user.setId(2L);
        user.setUserType(UserTypes.ADMIN);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.getSingleVehicle(user, 1L);

        assertTrue(result.isLeft());
        assertEquals(400, result.getLeft().getCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(vehicleRepository, times(0)).findById(anyLong());
    }

    // Verifica che il metodo ritorni un errore 404 se il veicolo non viene trovato.
    @Test
    void testGetSingleVehicle_VehicleNotFound() {
        UserEntity user = new UserEntity();
        user.setId(3L);
        user.setUserType(UserTypes.BUYER);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.getSingleVehicle(user, 1L);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(vehicleRepository, times(1)).findById(1L);
    }

    // Verifica che il metodo ritorni con successo il DTO del veicolo se l'utente è autorizzato e il veicolo esiste.
    @Test
    void testGetSingleVehicle_Success() {
        UserEntity user = new UserEntity();
        user.setId(4L);
        user.setUserType(UserTypes.SELLER);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.getSingleVehicle(user, 1L);

        assertTrue(result.isRight());
        verify(userRepository, times(1)).findById(user.getId());
        verify(vehicleRepository, times(1)).findById(1L);
    }

    // Verifica che il metodo ritorni un errore 404 se non viene trovato alcun veicolo.
    @Test
    void testGetAllVehicle_NoVehiclesFound() {
        when(vehicleRepository.findAll()).thenReturn(new ArrayList<>());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleCRUDService.getAllVehicle();

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        verify(vehicleRepository, times(1)).findAll();
    }

    // Verifica che il metodo ritorni correttamente una lista di VehicleDTO se vengono trovati dei veicoli.
    @Test
    void testGetAllVehicle_VehiclesFound() {
        List<VehicleEntity> vehicleEntities = List.of(
                new VehicleEntity(
                        1L, "Toyota", "Yaris", 1618, "Grey", 280, "Manual",
                        2021, "Gasoline", BigDecimal.valueOf(52000), BigDecimal.valueOf(5),
                        Collections.singletonList("Air Conditioning"), true, null, null),

                new VehicleEntity(2L, "Fiat", "Panda", 999, "Red", 70, "Manual",
                        2020, "Hybrid", BigDecimal.valueOf(15500), BigDecimal.valueOf(5),
                        Collections.singletonList("Sunroof"), true, null, null)
        );

        when(vehicleRepository.findAll()).thenReturn(vehicleEntities);

        List<VehicleModel> vehicleModels = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> expectedVehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .toList();

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleCRUDService.getAllVehicle();

        assertTrue(result.isRight());
        assertEquals(expectedVehicleDTOs.size(), result.get().size());
        verify(vehicleRepository, times(1)).findAll();
    }

    // Verifica che il metodo ritorni un errore 403 se l'utente non è un amministratore.
    @Test
    void testUpdateVehicle_UserNotAdmin() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUserType(UserTypes.BUYER);

        VehicleRequest request = new VehicleRequest();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.updateVehicle(user, 1L, request);

        assertTrue(result.isLeft());
        assertEquals(403, result.getLeft().getCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(vehicleRepository, times(0)).findById(anyLong());
    }

    // Verifica che il metodo ritorni un errore 404 se il veicolo non viene trovato.
    @Test
    void testUpdateVehicle_VehicleNotFound() {
        UserEntity adminUser = new UserEntity();
        adminUser.setId(2L);
        adminUser.setUserType(UserTypes.ADMIN);

        VehicleRequest request = new VehicleRequest();

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.updateVehicle(adminUser, 1L, request);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        verify(userRepository, times(1)).findById(adminUser.getId());
        verify(vehicleRepository, times(1)).findById(1L);
    }

    // Verifica che il metodo aggiorni correttamente il veicolo e ritorni un DTO del veicolo aggiornato se l'utente è un amministratore e il veicolo esiste.
    @Test
    void testUpdateVehicle_Success() {
        UserEntity adminUser = new UserEntity();
        adminUser.setId(3L);
        adminUser.setUserType(UserTypes.ADMIN);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);

        VehicleRequest request = new VehicleRequest();
        request.setBrand("Ferrari");
        request.setModel("Roma");
        request.setDisplacement((int) 3.8);
        request.setColor("Red");
        request.setPower(620);
        request.setTransmission("Manual");
        request.setRegistrationYear(2021);
        request.setPowerSupply("Gasoline");
        request.setPrice(BigDecimal.valueOf(250000));
        request.setDiscount(BigDecimal.valueOf(2));
        request.setAccessories(Collections.singletonList("Air Conditioning"));
        request.setIsNew(true);
        request.setVehicleStatus("rentable");
        request.setVehicleType("car");

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.saveAndFlush(any(VehicleEntity.class))).thenReturn(vehicleEntity);

        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.updateVehicle(adminUser, 1L, request);

        assertTrue(result.isRight());
        verify(userRepository, times(1)).findById(adminUser.getId());
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).saveAndFlush(vehicleEntity);
    }

    // Verifica che il metodo ritorni un errore 403 se l'utente non è un amministratore.
    @Test
    void testDeleteVehicle_UserNotAdmin() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUserType(UserTypes.BUYER);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        VehicleErrorResponse result = vehicleCRUDService.deleteVehicle(user, 1L);

        assertEquals(403, result.getCode());
        verify(userRepository, times(1)).findById(user.getId());
        verify(vehicleRepository, times(0)).findById(anyLong());
        verify(vehicleRepository, times(0)).delete(any(VehicleEntity.class));
    }

    // Verifica che il metodo elimini correttamente il veicolo e ritorni un messaggio di successo se l'utente è un amministratore e il veicolo esiste.
    @Test
    void testDeleteVehicle_Success() {
        UserEntity adminUser = new UserEntity();
        adminUser.setId(3L);
        adminUser.setUserType(UserTypes.ADMIN);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        doReturn(Either.right(new VehicleDTO())).when(vehicleCRUDService).getSingleVehicle(adminUser, 1L);

        VehicleErrorResponse result = vehicleCRUDService.deleteVehicle(adminUser, 1L);

        assertEquals(200, result.getCode());
        verify(userRepository, times(1)).findById(adminUser.getId());
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).delete(vehicleEntity);
    }

    // Verifica che il metodo ritorni un errore 500 se si verifica un'eccezione durante l'eliminazione del veicolo.
    @Test
    void testDeleteVehicle_InternalServerError() {
        UserEntity adminUser = new UserEntity();
        adminUser.setId(4L);
        adminUser.setUserType(UserTypes.ADMIN);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        doReturn(Either.right(new VehicleDTO())).when(vehicleCRUDService).getSingleVehicle(adminUser, 1L);
        doThrow(new RuntimeException("Error deleting vehicle")).when(vehicleRepository).delete(vehicleEntity);

        VehicleErrorResponse result = vehicleCRUDService.deleteVehicle(adminUser, 1L);

        assertEquals(500, result.getCode());
        verify(userRepository, times(1)).findById(adminUser.getId());
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).delete(vehicleEntity);
    }
}