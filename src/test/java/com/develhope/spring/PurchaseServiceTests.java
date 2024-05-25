package com.develhope.spring;

import com.develhope.spring.purchase.DTO.PurchaseDTO;
import com.develhope.spring.purchase.entities.PurchaseEntity;
import com.develhope.spring.purchase.entities.PurchasesLinkEntity;
import com.develhope.spring.purchase.repositories.PurchaseRepository;
import com.develhope.spring.purchase.repositories.PurchasesLinkRepository;
import com.develhope.spring.purchase.request.PurchaseRequest;
import com.develhope.spring.purchase.response.PurchaseResponse;
import com.develhope.spring.purchase.services.PurchaseService;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PurchasesLinkRepository purchasesLinkRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createPurchaseSuccess() {
        UserEntity seller = new UserEntity(1L, "pietro", "tornasole", "+31",
                "culobello@si.of", "igattini", UserTypes.SELLER);

        UserEntity buyer = new UserEntity(2L, "pietro", "tornasole", "+31",
                "culobello@si.of", "igattini", UserTypes.BUYER);

        VehicleEntity vehicle= new VehicleEntity(3L, "Lamborghini", "Revuelto",  6, "Blue", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setVehicleId(3L);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setVehicle(vehicle);
        purchaseEntity.setPurchaseId(1L);

        when(vehicleRepository.findById(3L)).thenReturn(Optional.of(vehicle));
        when(purchaseRepository.save(any(PurchaseEntity.class))).thenReturn(purchaseEntity);
        when(userRepository.findById(2L)).thenReturn(Optional.of(buyer));
        when(purchasesLinkRepository.save(any(PurchasesLinkEntity.class))).thenReturn(new PurchasesLinkEntity());

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.create(seller, 2L, purchaseRequest);
        System.out.println(result);

        assertTrue(result.isRight());

        verify(vehicleRepository, times(1)).save(vehicle);
        verify(purchaseRepository, times(1)).save(any(PurchaseEntity.class));
        verify(purchasesLinkRepository, times(1)).save(any(PurchasesLinkEntity.class));
    }

    @Test
    public void createPurchaseInvalidRequest() {
        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.create(new UserEntity(), null, null);

        assertTrue(result.isLeft());
        assertEquals(400, result.getLeft().getCode());
        assertEquals("Invalid input parameters", result.getLeft().getMessage());
    }

    @Test
    public void createPurchaseInvalidVehicle() {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setVehicleId(1L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.create(new UserEntity(), null, purchaseRequest);

        assertTrue(result.isLeft());
        verify(vehicleRepository, times(1)).findById(1L);
        assertEquals(404, result.getLeft().getCode());
        assertEquals("No vehicle found with such Id", result.getLeft().getMessage());
    }

    @Test
    public void createPurchaseNotPurchasableVehicle() {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setVehicleId(1L);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);
        vehicleEntity.setVehicleStatus(VehicleStatus.ORDERABLE);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.create(new UserEntity(), null, purchaseRequest);

        assertTrue(result.isLeft());
        verify(vehicleRepository, times(1)).findById(1L);
        assertEquals(403, result.getLeft().getCode());
        assertEquals("Vehicle is not purchasable", result.getLeft().getMessage());
    }

    @Test
    public void createPurchaseSpecifiedBuyerNotFound() {
        UserEntity seller = new UserEntity();
        seller.setId(2L);
        seller.setUserType(UserTypes.SELLER);

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setVehicleId(1L);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1L);
        vehicleEntity.setVehicleStatus(VehicleStatus.PURCHASABLE);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicleEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.create(seller, 1L, purchaseRequest);

        assertTrue(result.isLeft());
        verify(vehicleRepository, times(1)).findById(1L);
        assertEquals(404, result.getLeft().getCode());
        assertEquals("Specified buyer not found", result.getLeft().getMessage());
    }

    @Test
    public void getSingleSuccessful() {
        UserEntity buyer = new UserEntity();
        buyer.setId(2L);
        buyer.setUserType(UserTypes.BUYER);

        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleId(2L);
        vehicle.setVehicleStatus(VehicleStatus.PURCHASABLE);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setPurchaseId(1L);
        purchaseEntity.setVehicle(vehicle);

        PurchasesLinkEntity purchasesLink = new PurchasesLinkEntity();
        purchasesLink.setBuyer(buyer);
        purchasesLink.setPurchase(purchaseEntity);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchaseEntity));
        when(purchasesLinkRepository.findByBuyer_Id(2L)).thenReturn(List.of(purchasesLink));

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.getSingle(buyer, 1L);

        assertTrue(result.isRight());
    }

    @Test
    public void getSinglePurchaseNotFound() {
        when(purchaseRepository.findById(1L)).thenReturn(Optional.empty());

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.getSingle(new UserEntity(), 1L);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("Purchase with id " + 1L + " not found", result.getLeft().getMessage());
    }

    @Test
    public void getSingleDoesNotBelongToUser() {
        UserEntity seller = new UserEntity();
        seller.setId(2L);
        seller.setUserType(UserTypes.BUYER);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setPurchaseId(1L);

        PurchasesLinkEntity purchasesLink = new PurchasesLinkEntity();
        UserEntity wrong = new UserEntity();
        wrong.setId(2L);
        purchasesLink.setBuyer(wrong);
        purchasesLink.setPurchase(purchaseEntity);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchaseEntity));
        when(purchasesLinkRepository.findByBuyer_Id(2L)).thenReturn(Collections.emptyList());

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.getSingle(seller, 1L);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("Purchase does not belong to specified user", result.getLeft().getMessage());
    }

    @Test
    public void getAllSuccessful() {
        UserEntity buyer = new UserEntity();
        buyer.setId(2L);
        buyer.setUserType(UserTypes.BUYER);

        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleId(2L);
        vehicle.setVehicleStatus(VehicleStatus.PURCHASABLE);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setPurchaseId(1L);
        purchaseEntity.setVehicle(vehicle);

        PurchasesLinkEntity purchasesLink = new PurchasesLinkEntity();
        purchasesLink.setBuyer(buyer);
        purchasesLink.setPurchase(purchaseEntity);

        when(purchasesLinkRepository.findByBuyer_Id(2L)).thenReturn(List.of(purchasesLink));

        Either<PurchaseResponse, List<PurchaseDTO>> result = purchaseService.getAll(buyer);

        assertTrue(result.isRight());
    }

    @Test
    public void getAllNoPurchasesFound() {
        UserEntity buyer = new UserEntity();
        buyer.setId(2L);
        buyer.setUserType(UserTypes.BUYER);

        when(purchasesLinkRepository.findByBuyer_Id(2L)).thenReturn(Collections.emptyList());

        Either<PurchaseResponse, List<PurchaseDTO>> result = purchaseService.getAll(buyer);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("Purchases not found", result.getLeft().getMessage());
    }

    @Test
    public void updateSuccessful(){}

    @Test
    public void updateVehicleNotFound() {}

    @Test
    public void deleteSuccessful(){}

    @Test
    public void deleteInternalError(){}

}
