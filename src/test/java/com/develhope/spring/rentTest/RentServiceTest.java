package com.develhope.spring.rentTest;

import com.develhope.spring.rent.DTO.RentDTO;
import com.develhope.spring.rent.entities.RentEntity;
import com.develhope.spring.rent.entities.RentLink;
import com.develhope.spring.rent.repositories.RentRepository;
import com.develhope.spring.rent.repositories.RentalsLinkRepository;
import com.develhope.spring.rent.request.RentRequest;
import com.develhope.spring.rent.response.RentResponse;
import com.develhope.spring.rent.services.RentService;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RentServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RentalsLinkRepository rentalsLinkRepository;

    @Mock
    private RentRepository rentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RentService rentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createRent_UserIsBuyer_Success() {
        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(LocalDate.now());
        rentRequest.setEndDate(LocalDate.now().plusDays(3));
        rentRequest.setDailyCost(BigDecimal.valueOf(50));
        rentRequest.setPaid(true);
        rentRequest.setVehicleId(1L);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.BUYER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);
        vehicleEntity.setVehicleStatus(VehicleStatus.RENTABLE);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));

        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);
        when(rentRepository.save(any(RentEntity.class))).thenReturn(new RentEntity());

        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest, null, userEntity);

        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
        verify(rentRepository, times(1)).save(any(RentEntity.class));
        verify(rentalsLinkRepository, times(1)).save(any(RentLink.class));

        assertTrue(result.isRight());
    }

    @Test
    public void createRent_UserTypeIsNotDefined_Failure() {

        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(LocalDate.now());
        rentRequest.setEndDate(LocalDate.now().plusDays(3));
        rentRequest.setDailyCost(BigDecimal.valueOf(50));
        rentRequest.setPaid(true);
        rentRequest.setVehicleId(1L);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        userEntity.setUserType(UserTypes.NOT_DEFINED);

        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest, null, userEntity);

        verifyNoInteractions(vehicleRepository, rentRepository, rentalsLinkRepository);

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(403, rentResponse.getStatusCode());
        assertEquals("User type is not defined", result.getLeft().getMessage()); // Check for expected message
    }

    @Test
    void updateRentDates_Success() {

        RentEntity existingRent = new RentEntity();
        existingRent.setId(1L);
        existingRent.setStartDate(LocalDate.now());
        existingRent.setEndDate(LocalDate.now().plusDays(3));
        existingRent.setDailyCost(BigDecimal.valueOf(50));
        existingRent.setIsPaid(true);
        existingRent.setActive(true);

        LocalDate newStartDate = LocalDate.now().plusDays(1);
        LocalDate newEndDate = LocalDate.now().plusDays(5);

        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(newStartDate);
        rentRequest.setEndDate(newEndDate);

        UserEntity userEntity = new UserEntity(1L,"carlo", "armato", "333", "culo@ok", "bellapass", UserTypes.BUYER);

        RentLink rentLink = new RentLink(userEntity, existingRent);
        when(rentalsLinkRepository.findByRentId(existingRent.getId())).thenReturn(Optional.of(rentLink));
        when(rentRepository.save(any(RentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Either<RentResponse, RentDTO> result = rentService.updateRentDates(existingRent.getId(), rentRequest, userEntity);
        System.out.println(result);

        assertTrue(result.isRight());
        RentDTO updatedRentDTO = result.get();
        assertEquals(newStartDate, updatedRentDTO.getStartDate());
        assertEquals(newEndDate, updatedRentDTO.getEndDate());
        assertEquals(existingRent.getId(), updatedRentDTO.getId());

        verify(rentRepository, times(1)).save(existingRent);
        verify(rentalsLinkRepository, times(1)).findByRentId(existingRent.getId());
    }

    @Test
    void updateRentDates_InvalidDates_Failure() {

        RentEntity existingRent = new RentEntity();
        existingRent.setId(1L);
        existingRent.setStartDate(LocalDate.now());
        existingRent.setEndDate(LocalDate.now().plusDays(3));
        existingRent.setDailyCost(BigDecimal.valueOf(50));
        existingRent.setIsPaid(true);
        existingRent.setActive(true);

        LocalDate newStartDate = LocalDate.now().plusDays(5);
        LocalDate newEndDate = LocalDate.now().plusDays(1);

        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(newStartDate);
        rentRequest.setEndDate(newEndDate);

        UserEntity userEntity = new UserEntity(1L,"carlo", "armato", "333", "culo@ok", "bellapass", UserTypes.BUYER);

        Either<RentResponse, RentDTO> result = rentService.updateRentDates(existingRent.getId(), rentRequest, userEntity);

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(400, rentResponse.getStatusCode());
        assertEquals("Start date must be before end date", rentResponse.getMessage());
    }

    @Test
    void updateRentDates_RentNotFound_Failure() {

        Long rentId = 1L;
        LocalDate newStartDate = LocalDate.now().plusDays(5);
        LocalDate newEndDate = LocalDate.now().plusDays(10);

        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(newStartDate);
        rentRequest.setEndDate(newEndDate);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(123L);

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.empty());

        Either<RentResponse, RentDTO> result = rentService.updateRentDates(rentId, rentRequest, userEntity);

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(404, rentResponse.getStatusCode());
        assertEquals("Rent not found", rentResponse.getMessage());

        verify(rentalsLinkRepository, times(1)).findByRentId(rentId);
    }

    @Test
    void deleteRent_Success() {

        Long rentId = 1L;
        UserEntity userEntityDetails = new UserEntity();
        userEntityDetails.setUserType(UserTypes.ADMIN);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);
        rentEntity.setActive(true);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleStatus(VehicleStatus.RENTED);
        rentEntity.setVehicle(vehicleEntity);

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));

        Either<RentResponse, Void> result = rentService.deleteRent(rentId, userEntityDetails);

        assertEquals(Either.right(null), result);

        verify(rentRepository).save(rentEntity);
        verify(rentalsLinkRepository).delete(rentLink);
        verify(vehicleRepository).save(vehicleEntity);
        assertEquals(VehicleStatus.RENTABLE, vehicleEntity.getVehicleStatus());
        assertEquals(false, rentEntity.isActive());
        assertEquals(null, rentEntity.getVehicle());
    }

    @Test
    public void deleteRent_NotAuthorized_Failure() {

        Long id = 123L;
        UserEntity userEntity = new UserEntity();
        userEntity.setUserType(UserTypes.BUYER);

        Either<RentResponse, Void> result = rentService.deleteRent(id, userEntity);

        assertTrue(result.isLeft());
        RentResponse response = result.getLeft();
        assertEquals(403, response.getStatusCode());
        assertEquals("Unauthorized user", response.getMessage());

        verify(rentalsLinkRepository, never()).delete(any());
        verify(rentRepository, never()).save(any());
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void getRentList_AdminOrSeller_Success() {

        UserEntity userEntity = new UserEntity();
        userEntity.setUserType(UserTypes.ADMIN);

        List<RentEntity> rentEntities = Arrays.asList(
                createRentEntity(1L, BigDecimal.valueOf(50)),
                createRentEntity(2L, BigDecimal.valueOf(20))
        );

        when(rentRepository.findAll()).thenReturn(rentEntities);

        List<RentDTO> result = rentService.getRentList(userEntity);

        verify(rentRepository, times(1)).findAll();

        assertEquals(rentEntities.size(), result.size());
        List<Long> rentIds = result.stream().map(RentDTO::getId).collect(Collectors.toList());
        assertTrue(rentIds.contains(1L));
        assertTrue(rentIds.contains(2L));
    }

    @Test
    void getRentList_Buyer_Success() {

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.BUYER);

        RentEntity rent1 = createRentEntity(1L, BigDecimal.valueOf(50));
        RentEntity rent2 = createRentEntity(2L, BigDecimal.valueOf(60));
        List<RentLink> rentLinks = Arrays.asList(
                new RentLink(userEntity, rent1),
                new RentLink(userEntity, rent2)
        );

        when(rentalsLinkRepository.findAllByBuyer_Id(userEntity.getId())).thenReturn(rentLinks);

        List<RentDTO> result = rentService.getRentList(userEntity);

        verify(rentalsLinkRepository, times(1)).findAllByBuyer_Id(userEntity.getId());

        assertEquals(rentLinks.size(), result.size());
        List<Long> rentIds = result.stream().map(RentDTO::getId).collect(Collectors.toList());
        assertTrue(rentIds.contains(1L));
        assertTrue(rentIds.contains(2L));
    }

    @Test
    void getRentList_UnauthorizedUser_EmptyList() {

        UserEntity unauthorizedUser = new UserEntity();
        unauthorizedUser.setId(1L);
        unauthorizedUser.setUserType(UserTypes.BUYER);

        MockitoAnnotations.openMocks(this);

        when(rentalsLinkRepository.findAllByBuyer_Id(unauthorizedUser.getId())).thenReturn(List.of());

        List<RentDTO> rentDTOList = rentService.getRentList(unauthorizedUser);

        assertEquals(0, rentDTOList.size());
    }

    @Test
    void getRentById_AdminOrSeller_Success() {

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.ADMIN);

        RentEntity rentEntity = createRentEntity(1L, BigDecimal.valueOf(50));

        when(rentRepository.findById(1L)).thenReturn(Optional.of(rentEntity));

        RentDTO result = rentService.getRentById(1L, userEntity);

        verify(rentRepository, times(1)).findById(1L);

        assertNotNull(result);
        assertEquals(rentEntity.getId(), result.getId());
    }

    @Test
    void getRentById_Buyer_Success() {

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.BUYER);

        RentLink rentLink = mock(RentLink.class);
        when(rentalsLinkRepository.findByRentId(1L)).thenReturn(Optional.of(rentLink));
        when(rentLink.getBuyer()).thenReturn(userEntity);

        RentEntity rentEntity = createRentEntity(1L, BigDecimal.valueOf(50));
        when(rentLink.getRent()).thenReturn(rentEntity);

        RentDTO result = rentService.getRentById(1L, userEntity);

        verify(rentalsLinkRepository, times(1)).findByRentId(1L);
        verify(rentLink, times(1)).getBuyer();
        verify(rentLink, times(1)).getRent();

        assertNotNull(result);
        assertEquals(rentEntity.getId(), result.getId());
    }

    @Test
    void getRentById_UnauthorizedUser_Null() {

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.NOT_DEFINED);

        RentDTO result = rentService.getRentById(1L, userEntity);

        verifyNoInteractions(rentRepository, rentalsLinkRepository);

        assertNull(result);
    }

    @Test
    void payRent_Success() {

        RentLink rentLink = new RentLink();
        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(1L);
        rentEntity.setIsPaid(false);
        rentLink.setRent(rentEntity);

        UserEntity userEntityDetails = new UserEntity();
        userEntityDetails.setUserType(UserTypes.BUYER);
        userEntityDetails.setId(123L);

        UserEntity buyer = new UserEntity();
        buyer.setId(123L);
        rentLink.setBuyer(buyer);

        when(rentalsLinkRepository.findByRentId(1L)).thenReturn(Optional.of(rentLink));
        when(rentRepository.save(rentEntity)).thenReturn(rentEntity);

        Either<RentResponse, String> result = rentService.payRent(1L, 123L, userEntityDetails);

        verify(rentalsLinkRepository, times(1)).findByRentId(1L);
        verify(rentRepository, times(1)).save(rentEntity);

        assertTrue(result.isRight());
        assertEquals("Payment successful. Total amount paid: " + rentEntity.getTotalCost() + ", enjoy your ride!", result.get());
    }

    @Test
    void payRent_AlreadyPaid_Failure() {
        Long rentId = 1L;
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserType(UserTypes.BUYER);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);
        rentEntity.setIsPaid(true);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);
        rentLink.setBuyer(userEntity);

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));

        Either<RentResponse, String> result = rentService.payRent(rentId, userId, userEntity);

        RentResponse expectedResponse = new RentResponse(400, "Rent already paid");
        Either<RentResponse, String> expectedEither = Either.left(expectedResponse);

        assertEquals(expectedEither, result);
        verify(rentalsLinkRepository, times(1)).findByRentId(rentId);
        verify(rentRepository, never()).save(rentEntity);
    }

    @Test
    void payRent_UnauthorizedUser_Failure() {

        RentLink rentLink = new RentLink();
        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(1L);
        rentEntity.setIsPaid(false);
        rentLink.setRent(rentEntity);
        rentLink.setBuyer(null);

        UserEntity userEntityDetails = new UserEntity();
        userEntityDetails.setUserType(UserTypes.BUYER);
        userEntityDetails.setId(123L);

        when(rentalsLinkRepository.findByRentId(1L)).thenReturn(Optional.of(rentLink));

        Either<RentResponse, String> result = rentService.payRent(1L, 123L, userEntityDetails);

        verify(rentalsLinkRepository, times(1)).findByRentId(1L);
        verifyNoInteractions(rentRepository);

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(403, rentResponse.getStatusCode());
        assertEquals("Unauthorized user", rentResponse.getMessage());
    }

    @Test
    public void deleteBooking_Success() {

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(1L);
        rentEntity.setActive(true);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleStatus(VehicleStatus.RENTABLE);
        rentEntity.setVehicle(vehicleEntity);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);
        when(rentalsLinkRepository.findByRentId(1L)).thenReturn(Optional.of(rentLink));

        Either<RentResponse, String> result = rentService.deleteBooking(1L, new UserEntity());

        assertTrue(result.isRight());
        assertEquals("Rent booking successfully set to inactive and vehicle status updated to RENTABLE.", result.get());

        assertFalse(rentEntity.isActive());

        assertEquals(VehicleStatus.RENTABLE, vehicleEntity.getVehicleStatus());

        verify(rentalsLinkRepository, times(1)).findByRentId(1L);
        verify(vehicleRepository, times(1)).save(vehicleEntity);
        verify(rentRepository, times(1)).save(rentEntity);
    }

    @Test
    public void deleteBooking_ActiveRent_Failure() {

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(1L);
        rentEntity.setActive(true);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);
        when(rentalsLinkRepository.findByRentId(1L)).thenReturn(Optional.of(rentLink));

        Either<RentResponse, String> result = rentService.deleteBooking(1L, new UserEntity());

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(500, rentResponse.getStatusCode());
        assertEquals("Internal Server Error: No vehicle associated with the rent", rentResponse.getMessage());

        verify(rentalsLinkRepository, times(1)).findByRentId(1L);
        verify(vehicleRepository, never()).save(any());
        verify(rentRepository, never()).save(any());
    }

    @Test
    public void deleteBooking_UnauthorizedUser_Failure() {

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(1L);
        rentEntity.setActive(true);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);
        when(rentalsLinkRepository.findByRentId(1L)).thenReturn(Optional.of(rentLink));

        UserEntity unauthorizedUser = new UserEntity();
        unauthorizedUser.setUserType(UserTypes.NOT_DEFINED);

        Either<RentResponse, String> result = rentService.deleteBooking(1L, unauthorizedUser);

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(500, rentResponse.getStatusCode());
        assertEquals("Internal Server Error: No vehicle associated with the rent", rentResponse.getMessage());

        verify(rentalsLinkRepository, times(1)).findByRentId(1L);
        verifyNoMoreInteractions(vehicleRepository);
    }

    // Utility method to create a mock RentEntity
    private RentEntity createRentEntity(Long id, BigDecimal dailyCost) {
        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(id);
        rentEntity.setDailyCost(dailyCost);

        // Creating a mock VehicleEntity
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(id);
        vehicleEntity.setBrand("Brand " + id);
        vehicleEntity.setModel("Model " + id);
        rentEntity.setVehicle(vehicleEntity);

        return rentEntity;
    }

}
