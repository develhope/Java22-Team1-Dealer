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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        assertEquals(vehicle.getVehicleId(), result.get().getVehicle().getVehicleId());

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
    public void updateSuccessful1(){
        UserEntity buyer = new UserEntity();
        buyer.setId(2L);
        buyer.setUserType(UserTypes.BUYER);

        VehicleEntity oldVehicle = new VehicleEntity();
        oldVehicle.setVehicleId(2L);
        oldVehicle.setVehicleStatus(VehicleStatus.PURCHASABLE);

        VehicleEntity newVehicle = new VehicleEntity();
        newVehicle.setVehicleId(3L);
        newVehicle.setVehicleStatus(VehicleStatus.PURCHASABLE);

        PurchaseEntity beforeUpdateEntity = new PurchaseEntity();
        beforeUpdateEntity.setPurchaseId(1L);
        beforeUpdateEntity.setVehicle(oldVehicle);

        PurchaseEntity afterUpdateEntity = new PurchaseEntity();
        afterUpdateEntity.setPurchaseId(1L);
        afterUpdateEntity.setVehicle(newVehicle);

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setVehicleId(newVehicle.getVehicleId());

        PurchasesLinkEntity purchasesLink = new PurchasesLinkEntity();
        purchasesLink.setBuyer(buyer);
        purchasesLink.setPurchase(beforeUpdateEntity);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(beforeUpdateEntity));
        when(purchasesLinkRepository.findByBuyer_Id(2L)).thenReturn(List.of(purchasesLink));
        when(vehicleRepository.findById(newVehicle.getVehicleId())).thenReturn(Optional.of(newVehicle));
        when(purchaseRepository.save(any(PurchaseEntity.class))).thenReturn(afterUpdateEntity);

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.update(buyer, beforeUpdateEntity.getPurchaseId(), purchaseRequest);

        assertTrue(result.isRight());
        assertEquals(3L, result.get().getVehicle().getVehicleId());

        verify(purchaseRepository, times(1)).findById(1L);
        verify(purchasesLinkRepository, times(1)).findByBuyer_Id(2L);
        verify(vehicleRepository, times(1)).findById(3L);
        verify(purchaseRepository, times(1)).save(any(PurchaseEntity.class));
    }

    @Test
    public void updateVehicleNotFound() {
        UserEntity buyer = new UserEntity();
        buyer.setId(2L);
        buyer.setUserType(UserTypes.BUYER);

        VehicleEntity oldVehicle = new VehicleEntity();
        oldVehicle.setVehicleId(2L);
        oldVehicle.setVehicleStatus(VehicleStatus.PURCHASABLE);

        VehicleEntity newVehicle = new VehicleEntity();
        newVehicle.setVehicleId(3L);
        newVehicle.setVehicleStatus(VehicleStatus.PURCHASABLE);

        PurchaseEntity beforeUpdateEntity = new PurchaseEntity();
        beforeUpdateEntity.setPurchaseId(1L);
        beforeUpdateEntity.setVehicle(oldVehicle);

        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setVehicleId(newVehicle.getVehicleId());

        PurchasesLinkEntity purchasesLink = new PurchasesLinkEntity();
        purchasesLink.setBuyer(buyer);
        purchasesLink.setPurchase(beforeUpdateEntity);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(beforeUpdateEntity));
        when(purchasesLinkRepository.findByBuyer_Id(2L)).thenReturn(List.of(purchasesLink));
        when(vehicleRepository.findById(newVehicle.getVehicleId())).thenReturn(Optional.empty());

        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.update(buyer, beforeUpdateEntity.getPurchaseId(), purchaseRequest);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("Vehicle not found", result.getLeft().getMessage());

        verify(purchaseRepository, times(1)).findById(1L);
        verify(purchasesLinkRepository, times(1)).findByBuyer_Id(2L);
        verify(vehicleRepository, times(1)).findById(3L);
    }

    @Test
    public void deleteSuccessful(){
        UserEntity buyer = new UserEntity();
        buyer.setId(2L);
        buyer.setUserType(UserTypes.BUYER);

        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleId(2L);
        vehicle.setVehicleStatus(VehicleStatus.PURCHASABLE);

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setPurchaseId(1L);
        purchase.setVehicle(vehicle);

        PurchasesLinkEntity purchasesLink = new PurchasesLinkEntity();
        purchasesLink.setBuyer(buyer);
        purchasesLink.setPurchase(purchase);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        when(purchasesLinkRepository.findByBuyer_Id(2L)).thenReturn(List.of(purchasesLink));
        when(purchasesLinkRepository.findByPurchase_PurchaseId(1L)).thenReturn(purchasesLink);

        PurchaseResponse result = purchaseService.delete(buyer, purchase.getPurchaseId());

        assertEquals(200, result.getCode());
        assertEquals("Purchase deleted successfully", result.getMessage());

        ArgumentCaptor<VehicleEntity> vehicleCaptor = ArgumentCaptor.forClass(VehicleEntity.class);
        verify(vehicleRepository).save(vehicleCaptor.capture());
        assertEquals(VehicleStatus.PURCHASABLE, vehicleCaptor.getValue().getVehicleStatus());
        verify(purchasesLinkRepository, times(1)).delete(purchasesLink);

        ArgumentCaptor<PurchaseEntity> purchaseCaptor = ArgumentCaptor.forClass(PurchaseEntity.class);
        verify(purchaseRepository).save(purchaseCaptor.capture());
        assertFalse(purchaseCaptor.getValue().getIsPaid());
        assertNull(purchaseCaptor.getValue().getVehicle());
    }

    @Test
    public void deleteInternalError(){
        final Long BUYER_ID = 2L;
        final Long PURCHASE_ID = 1L;
        final Long VEHICLE_ID = 3L;

        UserEntity buyer = new UserEntity();
        buyer.setId(BUYER_ID);
        buyer.setUserType(UserTypes.BUYER);

        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleId(VEHICLE_ID);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setPurchaseId(PURCHASE_ID);
        purchaseEntity.setVehicle(vehicle);
        purchaseEntity.setIsPaid(true);

        PurchasesLinkEntity purchasesLink = new PurchasesLinkEntity();
        purchasesLink.setBuyer(buyer);
        purchasesLink.setPurchase(purchaseEntity);

        when(purchaseRepository.findById(PURCHASE_ID)).thenReturn(Optional.of(purchaseEntity));
        when(purchasesLinkRepository.findByBuyer_Id(BUYER_ID)).thenReturn(List.of(purchasesLink));
        when(purchasesLinkRepository.findByPurchase_PurchaseId(PURCHASE_ID)).thenReturn(purchasesLink);
        doThrow(new RuntimeException("Database error")).when(purchasesLinkRepository).delete(any(PurchasesLinkEntity.class));

        PurchaseResponse response = purchaseService.delete(buyer, PURCHASE_ID);

        assertEquals(500, response.getCode());
        assertEquals("Internal server error", response.getMessage());

        ArgumentCaptor<VehicleEntity> vehicleCaptor = ArgumentCaptor.forClass(VehicleEntity.class);
        verify(vehicleRepository).save(vehicleCaptor.capture());
        assertEquals(VehicleStatus.PURCHASABLE, vehicleCaptor.getValue().getVehicleStatus());

        verify(purchasesLinkRepository, times(1)).delete(purchasesLink);
    }

}
