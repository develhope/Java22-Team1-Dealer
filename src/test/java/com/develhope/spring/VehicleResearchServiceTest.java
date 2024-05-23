package com.develhope.spring;

import com.develhope.spring.vehicles.DTO.VehicleDTO;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import com.develhope.spring.vehicles.response.VehicleErrorResponse;
import com.develhope.spring.vehicles.services.VehicleResearchService;
import io.vavr.control.Either;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class VehicleResearchServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleResearchService vehicleResearchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void deleteAll() {
        vehicleRepository.deleteAll();
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository.
    @Test
    public void testFindByColor_NoVehicles() {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor("Red");

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
    }

    // Verifica il comportamento del metodo quando nessun veicolo corrisponde al colore specificato.
    @Test
    public void testFindByColor_NoMatchingColor() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Lamborghini", "Revuelto", (int) 6.4, "Blue", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Toyota", "Yaris", 1618, "Black",
                280, "Manual", 2021, "Gasoline", BigDecimal.valueOf(52000), BigDecimal.valueOf(5),
                Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicleRepository.save(vehicle1);
        vehicleRepository.save(vehicle2);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor("Red");

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono al colore specificato.
    @Test
    public void testFindByColor_MatchingColor() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Toyota", "Yaris", 1618, "Red",
                280, "Manual", 2021, "Gasoline", BigDecimal.valueOf(52000), BigDecimal.valueOf(5),
                Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);

        when(vehicleRepository.findByColor("Red")).thenReturn(Arrays.asList(vehicle1, vehicle2));
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor("Red");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli di colori misti, ma solo alcuni corrispondono al colore specificato.
    @Test
    public void testFindByColor_MixedColors() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle3 = new VehicleEntity(3L, "Toyota", "Yaris", 1618, "Red",
                280, "Manual", 2021, "Gasoline", BigDecimal.valueOf(52000), BigDecimal.valueOf(5),
                Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        when(vehicleRepository.findByColor(anyString())).thenReturn(Arrays.asList(vehicle1, vehicle3));


        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor("Red");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(3L, vehicleDTOs.get(1).getVehicleId());
        assertEquals(vehicleDTOs.get(0).getColor(), "Red");
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository.
    @Test
    public void testFindByModel_NoVehicles() {
        when(vehicleRepository.findByModel("ModelX")).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel("ModelX");

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("No vehicle Found", result.getLeft().getMessage());
    }

    // Verifica il comportamento del metodo quando nessun veicolo corrisponde al modello specificato.

    @Test
    public void testFindByModel_NoMatchingModel() {
        when(vehicleRepository.findByModel("ModelX")).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel("ModelX");

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicle Found", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono al modello specificato.
    @Test
    public void testFindByModel_MatchingModel() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        when(vehicleRepository.findByModel("Panda")).thenReturn(Arrays.asList(vehicle1, vehicle2));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel("Panda");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli di modelli misti, ma solo alcuni corrispondono al modello specificato.
    @Test
    public void testFindByModel_MixedModels() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle3 = new VehicleEntity(3L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle4 = new VehicleEntity(4L, "Toyota", "Yaris", 1618, "Red",
                280, "Manual", 2021, "Gasoline", BigDecimal.valueOf(52000), BigDecimal.valueOf(5),
                Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);

        when(vehicleRepository.findByModel("Panda")).thenReturn(Arrays.asList(vehicle1, vehicle3));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel("Panda");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(3L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository.
    @Test
    public void testFindByBrand_NoVehicles() {
        when(vehicleRepository.findByBrand("Tesla")).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByBrand("Tesla");

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("No vehicle Found", result.getLeft().getMessage());
    }

    // Verifica il comportamento del metodo quando nessun veicolo corrisponde al brand specificato.
    @Test
    public void testFindByBrand_NoMatchingBrand() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);

        when(vehicleRepository.findByBrand(anyString())).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByBrand("Toyota");


        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("No vehicle Found", result.getLeft().getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono al brand specificato.
    @Test
    public void testFindByBrand_MatchingBrand() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        when(vehicleRepository.findByBrand("Fiat")).thenReturn(Arrays.asList(vehicle1, vehicle2));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByBrand("Fiat");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli di brand misti, ma solo alcuni corrispondono al brand specificato.
    @Test
    public void testFindByBrand_MixedBrands() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle3 = new VehicleEntity(3L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        when(vehicleRepository.findByBrand("Fiat")).thenReturn(Arrays.asList(vehicle1, vehicle3));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByBrand("Fiat");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(3L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository.
    @Test
    public void testFindByTransmission_NoVehicles() {
        when(vehicleRepository.findByTransmission("Automatic")).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByTransmission("Automatic");

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("No vehicle Found", result.getLeft().getMessage());
    }

    // Verifica il comportamento del metodo quando nessun veicolo corrisponde al tipo di cambio specificato.
    @Test
    public void testFindByTransmission_NoMatchingTransmission() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);

        when(vehicleRepository.findByTransmission("Automatic")).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByTransmission("Automatic");

        // Verifica
        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("No vehicle Found", result.getLeft().getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono al tipo di cambio specificato.
    @Test
    public void testFindByTransmission_MatchingTransmission() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        when(vehicleRepository.findByTransmission("Manual")).thenReturn(Arrays.asList(vehicle1, vehicle2));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByTransmission("Manual");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli con tipologie di cambio differenti, ma solo alcuni corrispondono al tipo di cambio specificato
    @Test
    public void testFindByTransmission_MixedTransmissions() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle3 = new VehicleEntity(3L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        when(vehicleRepository.findByTransmission("Manual")).thenReturn(Arrays.asList(vehicle1, vehicle3));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByTransmission("Manual");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(3L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository.
    @Test
    public void testFindByPowerSupply_NoVehicles() {
        when(vehicleRepository.findByPowerSupply("Electric")).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPowerSupply("Electric");

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicle Found", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando nessun veicolo corrisponde alla tipologia di alimentazione specificata.
    @Test
    public void testFindByPowerSupply_NoMatchingPowerSupply() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);

        when(vehicleRepository.findByPowerSupply("Electric")).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPowerSupply("Electric");

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("No vehicle Found", result.getLeft().getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono alla tipologia di alimentazione specificata.

    @Test
    public void testFindByPowerSupply_MatchingPowerSupply() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        when(vehicleRepository.findByPowerSupply("Gasoline")).thenReturn(Arrays.asList(vehicle1, vehicle2));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPowerSupply("Gasoline");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli con tipi di alimentazione misti, ma solo alcuni corrispondono alla tipologia di alimentazione specificata.
    @Test
    public void testFindByPowerSupply_MixedPowerSupplies() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle3 = new VehicleEntity(3L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);

        when(vehicleRepository.findByPowerSupply("Gasoline")).thenReturn(Arrays.asList(vehicle1, vehicle3));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPowerSupply("Gasoline");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(3L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica che findByAccessories ritorni un VehicleErrorResponse quando vehicleRepository restituisce una lista vuota.
    @Test
    void testFindByAccessories_NoVehiclesFound() {
        when(vehicleRepository.findByAccessoriesIn(anyList())).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByAccessories(Arrays.asList("GPS", "Airbag"));

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
        assertEquals("No vehicles found with the specified accessories", result.getLeft().getMessage());
    }

    // Verifica il comportamento del metodo quando nessun veicolo corrisponde agli accessori specificati.
    @Test
    public void testFindByAccessories_NoMatchingAccessories() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByAccessoriesIn(anyList())).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByAccessories(Arrays.asList("Cruise control", "Parking sensors"));

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found with the specified accessories", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono agli accessori specificati.
    @Test
    public void testFindByAccessories_MatchingAccessories() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Arrays.asList("Air Conditioning", "Cruise Control", "Parking Sensors"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Arrays.asList("Air Conditioning", "Cruise Control", "Parking Sensors"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByAccessoriesIn(anyList())).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByAccessories(List.of("Air Conditioning", "Cruise Control", "Parking Sensors"));

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando il valore minimo di cilindrata è maggiore del valore massimo.
    @Test
    public void testFindByDisplacement_InvalidRange() {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDisplacement(2000, 1000);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(400, errorResponse.getCode());
        assertEquals("the minimum displacement cannot be higher than the maximum", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository che corrispondono al range di cilindrata specificato.
    @Test
    public void testFindByDisplacement_NoVehicles() {
        when(vehicleRepository.findByDisplacementBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDisplacement(1000, 2000);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found in the specified range", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono al range di cilindrata specificato.
    @Test
    public void testFindByDisplacement_MatchingVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 1100, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 1500, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByDisplacementBetween(anyInt(), anyInt())).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDisplacement(1000, 2000);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando c'è un singolo veicolo che corrisponde al range di cilindrata specificato.
    @Test
    public void testFindByDisplacement_SingleMatchingVehicle() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 1100, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByDisplacementBetween(anyInt(), anyInt())).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDisplacement(1000, 1200);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Verifica il comportamento del metodo quando il valore minimo di potenza è maggiore del valore massimo.
    @Test
    public void testFindByPower_InvalidRange() {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPower(200, 100);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(400, errorResponse.getCode());
        assertEquals("the minimum power cannot be higher than the maximum", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository che corrispondono al range di potenza specificato.
    @Test
    public void testFindByPower_NoVehicles() {
        when(vehicleRepository.findByPowerBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPower(100, 200);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found in the specified range", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono al range di potenza specificato.
    @Test
    public void testFindByPower_MatchingVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByPowerBetween(anyInt(), anyInt())).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPower(60, 100);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando c'è un singolo veicolo che corrisponde al range di potenza specificato.
    @Test
    public void testFindByPower_SingleMatchingVehicle() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByPowerBetween(anyInt(), anyInt())).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPower(80, 90);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Verifica il comportamento del metodo quando il valore minimo dell'anno di immatricolazione è maggiore del valore massimo.
    @Test
    public void testFindByRegistrationYear_InvalidRange() {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByRegistrationYear(2021, 2020);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(400, errorResponse.getCode());
        assertEquals("the minimum registration year cannot be higher than the maximum", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository che corrispondono all'intervallo di anni di immatricolazione specificato.
    @Test
    public void testFindByRegistrationYear_NoVehicles() {
        when(vehicleRepository.findByRegistrationYearBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByRegistrationYear(2010, 2020);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found in the specified range", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono all'intervallo di anni di immatricolazione specificato.
    @Test
    public void testFindByRegistrationYear_MatchingVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByRegistrationYearBetween(anyInt(), anyInt())).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByRegistrationYear(2020, 2021);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando c'è un singolo veicolo che corrisponde all'anno di immatricolazione specificato.
    @Test
    public void testFindByRegistrationYear_SingleMatchingVehicle() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByRegistrationYearBetween(anyInt(), anyInt())).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByRegistrationYear(2021, 2021);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Verifica il comportamento del metodo quando il valore minimo del prezzo è maggiore del valore massimo.
    @Test
    public void testFindByPrice_InvalidRange() {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPrice(BigDecimal.valueOf(30000), BigDecimal.valueOf(20000));

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(400, errorResponse.getCode());
        assertEquals("the minimum price cannot be higher than the maximum", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository che corrispondono all'intervallo di prezzo specificato.
    @Test
    public void testFindByPrice_NoVehicles() {
        when(vehicleRepository.findByPriceBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPrice(BigDecimal.valueOf(10000), BigDecimal.valueOf(20000));

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found in the specified range", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono all'intervallo di prezzo specificato.
    @Test
    public void testFindByPrice_MatchingVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(15000),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(18000),
                BigDecimal.valueOf(5), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByPriceBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPrice(BigDecimal.valueOf(10000), BigDecimal.valueOf(20000));

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando c'è un singolo veicolo che corrisponde all'intervallo di prezzo specificato.
    @Test
    public void testFindByPrice_SingleMatchingVehicle() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(15000),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByPriceBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPrice(BigDecimal.valueOf(14000), BigDecimal.valueOf(16000));

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Verifica il comportamento del metodo quando il valore minimo del prezzo scontato è maggiore del valore massimo.
    @Test
    public void testFindByDiscount_InvalidRange() {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDiscount(BigDecimal.valueOf(5000), BigDecimal.valueOf(3000));

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(400, errorResponse.getCode());
        assertEquals("the minimum discount price cannot be higher than the maximum", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository che corrispondono all'intervallo di prezzo scontato specificato.
    @Test
    public void testFindByDiscount_NoVehicles() {
        when(vehicleRepository.findByDiscountBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDiscount(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found in the specified range", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono all'intervallo di prezzo scontato specificato.
    @Test
    public void testFindByDiscount_MatchingVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(1500), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByDiscountBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDiscount(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando c'è un singolo veicolo che corrisponde all'intervallo di prezzo scontato specificato.
    @Test
    public void testFindByDiscount_SingleMatchingVehicle() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByDiscountBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDiscount(BigDecimal.valueOf(900), BigDecimal.valueOf(1100));

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository che corrispondono al valore booleano specificato.
    @Test
    public void testFindByIsNew_NoVehicles() {
        when(vehicleRepository.findByIsNew(anyBoolean())).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByIsNew(true);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono al valore booleano specificato.
    @Test
    public void testFindByIsNew_MatchingVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(1500), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByIsNew(true)).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByIsNew(true);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando c'è un singolo veicolo che corrisponde al valore booleano specificato.
    @Test
    public void testFindByIsNew_SingleMatchingVehicle() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByIsNew(true)).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByIsNew(true);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nuovi, ma ci sono veicoli usati nel repository.
    @Test
    public void testFindByIsNew_NoNewVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), false, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByIsNew(false)).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByIsNew(false);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository che corrispondono allo stato specificato.
    @Test
    public void testFindByVehicleStatus_NoVehicles() {
        when(vehicleRepository.findByVehicleStatus(any(VehicleStatus.class))).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleStatus(VehicleStatus.PURCHASABLE);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found", errorResponse.getMessage());
    }

    // Verifica il comportamento del metodo quando ci sono veicoli che corrispondono allo stato specificato.
    @Test
    public void testFindByVehicleStatus_MatchingVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(1500), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByVehicleStatus(VehicleStatus.PURCHASABLE)).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleStatus(VehicleStatus.PURCHASABLE);

        assertTrue(result.isRight());
        //List<VehicleDTO> vehicleDTOs = result.getOrElse(Collections.emptyList());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando c'è un singolo veicolo che corrisponde allo stato specificato.
    @Test
    public void testFindByVehicleStatus_SingleMatchingVehicle() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByVehicleStatus(VehicleStatus.PURCHASABLE)).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleStatus(VehicleStatus.PURCHASABLE);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli con lo stato specificato.
    @Test
    public void testFindByVehicleStatus_NoMatchingVehicles() {
        when(vehicleRepository.findByVehicleStatus(VehicleStatus.RENTABLE)).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleStatus(VehicleStatus.PURCHASABLE);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found", errorResponse.getMessage());
    }

    // Verifica che il metodo gestisca correttamente il caso in cui non ci sono veicoli nel repository che corrispondono al tipo di veicolo specificato.
    // Si aspetta di ricevere un errore 404 con un messaggio appropriato.
    @Test
    public void testFindByVehicleType_NoVehicles() {
        when(vehicleRepository.findByVehicleType(any(VehicleType.class))).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleType(VehicleType.CAR);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found", errorResponse.getMessage());
    }

    // Verifica che il metodo restituisca correttamente una lista di veicoli quando ci sono veicoli nel repository che corrispondono al tipo di veicolo specificato.
    // Si aspetta di ricevere una lista di VehicleDTO contenente i dettagli dei veicoli corrispondenti.
    @Test
    public void testFindByVehicleType_MatchingVehicles() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(1500), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        vehicles.add(vehicle2);
        when(vehicleRepository.findByVehicleType(VehicleType.CAR)).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleType(VehicleType.CAR);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(2L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica che il metodo gestisca correttamente il caso in cui c'è un solo veicolo nel repository che corrisponde al tipo di veicolo specificato.
    // Si aspetta di ricevere una lista di VehicleDTO contenente i dettagli di quel veicolo.
    @Test
    public void testFindByVehicleType_SingleMatchingVehicle() {
        List<VehicleEntity> vehicles = new ArrayList<>();
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Fiat", "Panda", 875, "Red", 85,
                "Manual", 2021, "Gasoline", BigDecimal.valueOf(23900),
                BigDecimal.valueOf(1000), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        vehicles.add(vehicle1);
        when(vehicleRepository.findByVehicleType(VehicleType.CAR)).thenReturn(vehicles);

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleType(VehicleType.CAR);

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(1, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.getFirst().getVehicleId());
    }

    // Il test verifica che il metodo findByVehicleType nella classe VehicleResearchService restituisca un errore con il codice 404
    // e un messaggio appropriato quando viene fornito un tipo di veicolo non definito nell'enumerazione VehicleType.
    @Test
    public void testFindByVehicleType_NotDefinedVehicleType() {
        when(vehicleRepository.findByVehicleType(VehicleType.NOT_DEFINED)).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleType(VehicleType.NOT_DEFINED);

        assertTrue(result.isLeft());
        VehicleErrorResponse errorResponse = result.getLeft();
        assertEquals(404, errorResponse.getCode());
        assertEquals("No vehicles found", errorResponse.getMessage());
    }
}
