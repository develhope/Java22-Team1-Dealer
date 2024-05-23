package com.develhope.spring.rentTest;

import com.develhope.spring.rent.entities.RentLink;
import com.develhope.spring.rent.model.RentModel;
import com.develhope.spring.rent.repositories.RentalsLinkRepository;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.rent.DTO.RentDTO;
import com.develhope.spring.rent.entities.RentEntity;
import com.develhope.spring.rent.repositories.RentRepository;
import com.develhope.spring.rent.request.RentRequest;
import com.develhope.spring.rent.response.RentResponse;
import com.develhope.spring.rent.services.RentService;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        // Mocking rent request and user details
        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(LocalDate.now());
        rentRequest.setEndDate(LocalDate.now().plusDays(3));
        rentRequest.setDailyCost(BigDecimal.valueOf(50));
        rentRequest.setPaid(true);
        rentRequest.setVehicleId(1L); // Assuming valid vehicle ID

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.BUYER); // User is a buyer

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Mocking vehicle entity
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);
        vehicleEntity.setVehicleStatus(VehicleStatus.RENTABLE); // Vehicle is rentable

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));

        // Mocking save methods
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);
        when(rentRepository.save(any(RentEntity.class))).thenReturn(new RentEntity());

        // Call the method
        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest, null, userEntity);

        // Verify interactions
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
        verify(rentRepository, times(1)).save(any(RentEntity.class));
        verify(rentalsLinkRepository, times(1)).save(any(RentLink.class));

        // Assert result
        assertTrue(result.isRight()); // Result should be on the right side
    }

    @Test
    void createRent_UserIsNotBuyer_Failure() {
        // Mocking userEntityDetails
        UserEntity userEntityDetails = new UserEntity();
        userEntityDetails.setUserType(UserTypes.ADMIN); // Assuming user is not a buyer

        // Mocking userId
        Long userId = 123L;

        // Mocking rentRequest
        RentRequest rentRequest = new RentRequest(
                LocalDate.now(), LocalDate.now().plusDays(3),
                BigDecimal.valueOf(50), true, 1L, userId);

        // Mocking userCheck
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        Either<RentResponse, UserEntity> userCheck = Either.right(userEntity);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(rentService.checkUserExists(userId)).thenReturn(userCheck);
        when(rentService.checkUserAuthorization(userEntityDetails)).thenReturn(Either.right(null)); // Assuming user is authorized

        // Mocking vehicleEntityOptional
        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);
        vehicleEntity.setVehicleStatus(VehicleStatus.RENTABLE);
        Optional<VehicleEntity> vehicleEntityOptional = Optional.of(vehicleEntity);

        when(vehicleRepository.findById(rentRequest.getVehicleId())).thenReturn(vehicleEntityOptional);

        // Mocking savedRentEntity
        RentEntity savedRentEntity = new RentEntity();
        savedRentEntity.setId(1L);
        savedRentEntity.setStartDate(rentRequest.getStartDate());
        savedRentEntity.setEndDate(rentRequest.getEndDate());
        savedRentEntity.setDailyCost(rentRequest.getDailyCost());
        savedRentEntity.setIsPaid(rentRequest.isPaid());
        savedRentEntity.setVehicleId(vehicleEntity);
        savedRentEntity.setTotalCost(BigDecimal.valueOf(150)); // Assuming totalCost

        when(rentRepository.save(any(RentEntity.class))).thenReturn(savedRentEntity);

        // Call the method under test
        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest, userId, userEntityDetails);

        // Verify the result
        assertEquals(Either.left(new RentResponse(403, "BUYER users can only create rents for themselves")), result);
        verify(userRepository, times(1)).findById(userId);
        verify(rentService, times(1)).checkUserAuthorization(userEntityDetails);
        verify(vehicleRepository, times(1)).findById(rentRequest.getVehicleId());
        verify(rentRepository, times(0)).save(any(RentEntity.class)); // No rent should be saved
    }

    @Test
    void createRent_UserNotFound_Failure() {
        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(LocalDate.now());
        rentRequest.setEndDate(LocalDate.now().plusDays(3));
        rentRequest.setDailyCost(BigDecimal.valueOf(50));
        rentRequest.setPaid(true);
        rentRequest.setVehicleId(1L);

        when(vehicleRepository.findById(any())).thenReturn(Optional.of(new VehicleEntity()));
        when(rentRepository.save(any())).thenReturn(new RentEntity());

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest, 1L, new UserEntity());

        verify(vehicleRepository, times(1)).findById(any());
        verify(rentRepository, times(0)).save(any());
        verify(userRepository, times(1)).findById(any());
        assertEquals(Either.Left.class, result.getClass());
        assertEquals(404, result.getLeft().getStatusCode());
    }

    @Test
    void createRent_UserIsBuyerAndNoUserId_Success() {
        UserEntity buyerUser = new UserEntity();
        buyerUser.setId(1L);
        buyerUser.setUserType(UserTypes.BUYER);

        RentRequest rentRequest = new RentRequest();
        rentRequest.setVehicleId(1L);
        rentRequest.setStartDate(LocalDate.now());
        rentRequest.setEndDate(LocalDate.now().plusDays(7));
        rentRequest.setDailyCost(BigDecimal.valueOf(100));
        rentRequest.setPaid(true);

        when(userRepository.findById(buyerUser.getId())).thenReturn(Optional.of(buyerUser));
        when(vehicleRepository.findById(rentRequest.getVehicleId())).thenReturn(Optional.of(new VehicleEntity()));

        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest, null, buyerUser);

        assertTrue(result.isRight());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void createRent_UserNotAuthenticated_Failure() {
        UserEntity unauthenticatedUser = new UserEntity();

        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleId(1L);
        vehicle.setVehicleStatus(VehicleStatus.RENTABLE);

        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(LocalDate.now());
        rentRequest.setEndDate(LocalDate.now().plusDays(3));
        rentRequest.setDailyCost(BigDecimal.valueOf(50));
        rentRequest.setPaid(true);
        rentRequest.setVehicleId(vehicle.getVehicleId());

        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest, null, unauthenticatedUser);

        assertTrue(result.isLeft());
        assertEquals(403, result.getLeft().getStatusCode());
        assertEquals("User not authenticated", result.getLeft().getMessage());
    }

    @Test
    void updateRentDates_Success() {
        RentEntity existingRent = new RentEntity();
        existingRent.setId(1L);
        existingRent.setStartDate(LocalDate.now());
        existingRent.setEndDate(LocalDate.now().plusDays(3));
        existingRent.setDailyCost(BigDecimal.valueOf(50));
        existingRent.setIsPaid(true);

        LocalDate newStartDate = LocalDate.now().plusDays(1);
        LocalDate newEndDate = LocalDate.now().plusDays(5);

        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(newStartDate);
        rentRequest.setEndDate(newEndDate);

        Long userId = 123L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        RentLink rentLink = new RentLink(userEntity, existingRent);
        when(rentalsLinkRepository.findByRentId(existingRent.getId())).thenReturn(Optional.of(rentLink));

        when(rentRepository.findById(existingRent.getId())).thenReturn(Optional.of(existingRent));
        when(rentRepository.save(any(RentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Either<RentResponse, RentDTO> result = rentService.updateRentDates(existingRent.getId(), rentRequest, userEntity);

        assertTrue(result.isRight());
        RentDTO updatedRentDTO = result.get();
        assertEquals(newStartDate, updatedRentDTO.getStartDate());
        assertEquals(newEndDate, updatedRentDTO.getEndDate());
        assertEquals(existingRent.getId(), updatedRentDTO.getId());

        verify(rentRepository, times(1)).findById(existingRent.getId());
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

        LocalDate newStartDate = LocalDate.now().plusDays(5);
        LocalDate newEndDate = LocalDate.now().plusDays(1);

        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(newStartDate);
        rentRequest.setEndDate(newEndDate);

        Long userId = 123L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        RentLink rentLink = new RentLink(userEntity, existingRent);
        when(rentalsLinkRepository.findByRentId(existingRent.getId())).thenReturn(Optional.of(rentLink));

        when(rentRepository.findById(existingRent.getId())).thenReturn(Optional.of(existingRent));

        Either<RentResponse, RentDTO> result = rentService.updateRentDates(existingRent.getId(), rentRequest, userEntity);

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(400, rentResponse.getStatusCode());
        assertEquals("Start date must be before end date", rentResponse.getMessage());

        verify(rentRepository, times(1)).findById(existingRent.getId());
        verify(rentRepository, times(0)).save(any(RentEntity.class));
        verify(rentalsLinkRepository, times(1)).findByRentId(existingRent.getId());
    }

    @Test
    void updateRentDates_RentNotFound_Failure() {
        Long rentId = 1L;

        LocalDate newStartDate = LocalDate.now().plusDays(5);
        LocalDate newEndDate = LocalDate.now().plusDays(10);

        RentRequest rentRequest = new RentRequest();
        rentRequest.setStartDate(newStartDate);
        rentRequest.setEndDate(newEndDate);

        Long userId = 123L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        RentLink rentLink = new RentLink(userEntity, new RentEntity());
        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));

        when(rentRepository.findById(rentId)).thenReturn(Optional.empty());

        Either<RentResponse, RentDTO> result = rentService.updateRentDates(rentId, rentRequest, userEntity);

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(404, rentResponse.getStatusCode());
        assertEquals("Rent not found", rentResponse.getMessage());

        verify(rentRepository, times(1)).findById(rentId);
        verify(rentRepository, times(0)).save(any(RentEntity.class));
        verify(rentalsLinkRepository, times(1)).findByRentId(rentId);
    }

    @Test
    void deleteRent_Success() {
        Long rentId = 1L;

        Long userId = 123L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        RentLink rentLink = new RentLink(userEntity, new RentEntity());
        when(rentalsLinkRepository.findByRentIdAndBuyerId(rentId, userId)).thenReturn(Optional.of(rentLink));

        when(rentRepository.findById(rentId)).thenReturn(Optional.of(new RentEntity()));

        Either<RentResponse, Void> resultEither = rentService.deleteRent(rentId, userEntity);

        assertTrue(resultEither.isRight());

        verify(rentRepository, times(1)).findById(rentId);
        verify(rentRepository, times(1)).delete(any(RentEntity.class));
        verify(rentalsLinkRepository, times(1)).findByRentIdAndBuyerId(rentId, userId);
        verify(rentalsLinkRepository, times(1)).delete(rentLink);
    }

    @Test
    void deleteRent_NotAuthorized_Failure() {
        Long rentId = 1L;

        Long userId = 123L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserType(UserTypes.BUYER);

        RentLink rentLink = new RentLink(userEntity, new RentEntity());
        when(rentalsLinkRepository.findByRentIdAndBuyerId(rentId, userId)).thenReturn(Optional.of(rentLink));

        when(rentRepository.findById(rentId)).thenReturn(Optional.of(new RentEntity()));

        Either<RentResponse, Void> resultEither = rentService.deleteRent(rentId, userEntity);

        assertTrue(resultEither.isLeft());
        assertEquals(404, resultEither.getLeft().getStatusCode());

        verify(rentRepository, never()).delete(any(RentEntity.class));
        verify(rentalsLinkRepository, never()).delete(any(RentLink.class));
    }

    @Test
    void getRentList_AdminOrSeller_Success() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(123L);
        userEntity.setUserType(UserTypes.ADMIN);

        List<RentEntity> rentEntities = List.of(
                new RentEntity(),
                new RentEntity()
        );

        List<RentLink> rentLinks = List.of(
                new RentLink(userEntity, rentEntities.get(0)),
                new RentLink(userEntity, rentEntities.get(1))
        );

        when(rentalsLinkRepository.findAllBySeller_Id(userEntity.getId())).thenReturn(rentLinks);
        when(rentRepository.findById(anyLong())).thenReturn(Optional.of(new RentEntity()));

        List<RentDTO> result = rentService.getRentList(userEntity);

        assertNotNull(result);

        verify(rentalsLinkRepository, times(1)).findAllBySeller_Id(userEntity.getId());
        verify(rentRepository, times(rentEntities.size())).findById(anyLong());
    }

    @Test
    void getRentList_Buyer_Success() {
        UserEntity buyer = new UserEntity();
        buyer.setId(1L);
        buyer.setUserType(UserTypes.BUYER);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(1L);
        rentEntity.setStartDate(LocalDate.now());
        rentEntity.setEndDate(LocalDate.now().plusDays(5));
        rentEntity.setDailyCost(BigDecimal.valueOf(50));
        rentEntity.setTotalCost(BigDecimal.valueOf(250));
        rentEntity.setIsPaid(true);
        rentEntity.setActive(true);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);
        vehicleEntity.setVehicleStatus(VehicleStatus.RENTABLE);

        RentLink rentLink = new RentLink(buyer, rentEntity);

        List<RentLink> rentLinks = Collections.singletonList(rentLink);

        when(rentalsLinkRepository.findAllByBuyer_Id(buyer.getId())).thenReturn(rentLinks);
        when(rentRepository.findById(rentEntity.getId())).thenReturn(Optional.of(rentEntity));
        when(vehicleRepository.findById(vehicleEntity.getVehicleId())).thenReturn(Optional.of(vehicleEntity));

        List<RentDTO> rentDTOList = rentService.getRentList(buyer);

        assertNotNull(rentDTOList);
        assertFalse(rentDTOList.isEmpty());
        assertEquals(1, rentDTOList.size());
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
        Long rentId = 1L;
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserType(UserTypes.ADMIN);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);
        rentEntity.setStartDate(LocalDate.now());
        rentEntity.setEndDate(LocalDate.now().plusDays(7));
        rentEntity.setDailyCost(BigDecimal.valueOf(50));
        rentEntity.setIsPaid(true);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);

        RentModel rentModel = RentModel.entityToModel(rentEntity);
        RentDTO expectedRentDTO = RentModel.modelToDTO(rentModel);

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));

        RentDTO result = rentService.getRentById(rentId, userEntity);

        assertEquals(expectedRentDTO, result);
        verify(rentalsLinkRepository, times(1)).findByRentId(rentId);
    }

    @Test
    void getRentById_Buyer_Success() {
        Long rentId = 1L;
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserType(UserTypes.BUYER);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);
        rentEntity.setStartDate(LocalDate.now());
        rentEntity.setEndDate(LocalDate.now().plusDays(7));
        rentEntity.setDailyCost(BigDecimal.valueOf(50));
        rentEntity.setIsPaid(true);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);

        RentModel rentModel = RentModel.entityToModel(rentEntity);
        RentDTO expectedRentDTO = RentModel.modelToDTO(rentModel);

        when(rentalsLinkRepository.findByRentIdAndBuyerId(rentId, userId)).thenReturn(Optional.of(rentLink));

        RentDTO result = rentService.getRentById(rentId, userEntity);

        assertEquals(expectedRentDTO, result);
        verify(rentalsLinkRepository, times(1)).findByRentIdAndBuyerId(rentId, userId);
    }

    @Test
    void getRentById_UnauthorizedUser_Null() {
        Long rentId = 1L;
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserType(UserTypes.BUYER);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));

        RentDTO result = rentService.getRentById(rentId, userEntity);

        assertNull(result);
        verify(rentalsLinkRepository, times(1)).findByRentId(rentId);
    }

    @Test
    void payRent_Success() {
        Long rentId = 1L;
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserType(UserTypes.BUYER);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);
        rentEntity.setIsPaid(false);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);
        rentLink.setBuyer(userEntity);

        String expectedPaymentMessage = "Payment successful. Total amount paid: 100.00, enjoy your ride!";

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));

        Either<RentResponse, String> result = rentService.payRent(rentId, userId, userEntity);

        assertTrue(result.isRight());
        assertEquals(expectedPaymentMessage, result.get());
        assertTrue(rentEntity.getIsPaid());
        assertTrue(rentLink.getRent().isActive());
        verify(rentRepository, times(1)).save(rentEntity);
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
        Long rentId = 1L;
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserType(UserTypes.BUYER);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);
        rentEntity.setIsPaid(false);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));

        Either<RentResponse, String> result = rentService.payRent(rentId, userId, userEntity);

        assertTrue(result.isLeft());
        RentResponse response = result.getLeft();
        assertEquals(403, response.getStatusCode());
        assertEquals("Unauthorized user", response.getMessage());

        verify(rentalsLinkRepository, times(1)).findByRentId(rentId);
        verifyNoMoreInteractions(rentalsLinkRepository);
        verifyNoInteractions(rentRepository);
    }

    @Test
    void deleteBooking_Success() {
        Long rentId = 1L;
        UserEntity userEntityDetails = new UserEntity();

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);
        rentEntity.setActive(true);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));
        when(rentRepository.save(rentEntity)).thenReturn(rentEntity);

        Either<RentResponse, String> result = rentService.deleteBooking(rentId, userEntityDetails);

        assertEquals("Rent booking successfully set to inactive and vehicle status updated to RENTABLE.", result.get());
        verify(rentRepository, times(1)).save(rentEntity);
        verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
    }

    @Test
    void deleteBooking_ActiveRent_Failure() {
        Long rentId = 1L;
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);
        rentEntity.setActive(true);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);

        RentResponse expectedResponse = new RentResponse(400, "Rent is Active and cannot be deleted");

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));


        Either<RentResponse, String> result = rentService.deleteBooking(rentId, userEntity);

        assertEquals(expectedResponse, result.getLeft());
        verify(rentalsLinkRepository, times(1)).findByRentId(rentId);
        verify(rentRepository, never()).save(rentEntity);
        verify(vehicleRepository, never()).save(any(VehicleEntity.class));
    }

    @Test
    void deleteBooking_UnauthorizedUser_Failure() {
        Long rentId = 1L;
        Long userId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        RentEntity rentEntity = new RentEntity();
        rentEntity.setId(rentId);

        RentLink rentLink = new RentLink();
        rentLink.setRent(rentEntity);

        when(rentalsLinkRepository.findByRentId(rentId)).thenReturn(Optional.of(rentLink));

        Either<RentResponse, String> result = rentService.deleteBooking(rentId, userEntity);

        assertTrue(result.isLeft());
        RentResponse rentResponse = result.getLeft();
        assertEquals(403, rentResponse.getStatusCode());
        assertEquals("Unauthorized user", rentResponse.getMessage());
        verify(rentalsLinkRepository, times(1)).findByRentId(rentId);
        verify(rentRepository, never()).save(any());
        verify(vehicleRepository, never()).save(any());
    }


    @Test
    void calculateTotalPrice_Success() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        BigDecimal dailyCost = BigDecimal.valueOf(50);

        RentModel rentModel = new RentModel(startDate, endDate, dailyCost, true, new VehicleEntity(), BigDecimal.ZERO);
        RentEntity rentEntity = RentModel.modelToEntity(rentModel);
        RentDTO expectedRentDTO = RentModel.modelToDTO(rentModel);

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal totalCost = dailyCost.multiply(BigDecimal.valueOf(days));

        when(rentRepository.save(any(RentEntity.class))).thenReturn(rentEntity);

        Either<RentResponse, RentDTO> result = rentService.createRent(null, null, null);

        assertTrue(result.isRight());
        RentDTO resultDTO = result.get();
        assertEquals(totalCost, resultDTO.getTotalCost());
        verify(rentRepository, times(1)).save(any(RentEntity.class));
    }

}
