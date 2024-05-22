package com.develhope.spring;

import com.develhope.spring.vehicles.DTO.VehicleDTO;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import com.develhope.spring.vehicles.response.VehicleErrorResponse;
import com.develhope.spring.vehicles.services.VehicleResearchService;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository.
    @Test
    public void testFindByColor_NoVehicles() {
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor("Red");

        assertTrue(result.isRight());
        assertTrue(result.get().isEmpty());
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
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor("Red");

        assertTrue(result.isRight());
        assertTrue(result.get().isEmpty());
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
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2));

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
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2, vehicle3));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor("Red");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(3L, vehicleDTOs.get(1).getVehicleId());
    }

    // Verifica il comportamento del metodo quando non ci sono veicoli nel repository.
    @Test
    public void testFindByModel_NoVehicles() {
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel("ModelX");

        assertTrue(result.isRight());
        assertTrue(result.get().isEmpty());
    }

    // Verifica il comportamento del metodo quando nessun veicolo corrisponde al modello specificato.
    @Test
    public void testFindByModel_NoMatchingModel() {
        VehicleEntity vehicle1 = new VehicleEntity(1L, "Lamborghini", "Revuelto", (int) 6.4, "Red", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        VehicleEntity vehicle2 = new VehicleEntity(2L, "Fiat", "Panda", 999, "Grey", 70,
                "Manual", 2020, "Hybrid", BigDecimal.valueOf(15500),
                BigDecimal.valueOf(5), Collections.singletonList("Sunroof"), true, VehicleStatus.PURCHASABLE, VehicleType.CAR);
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel("ModelX");

        assertTrue(result.isRight());
        assertTrue(result.get().isEmpty());
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
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2));

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
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2, vehicle3));

        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel("Panda");

        assertTrue(result.isRight());
        List<VehicleDTO> vehicleDTOs = result.get();
        assertEquals(2, vehicleDTOs.size());
        assertEquals(1L, vehicleDTOs.get(0).getVehicleId());
        assertEquals(3L, vehicleDTOs.get(1).getVehicleId());
    }

    //TODO findByBrand - findByTransmission - findByPowerSupply - findByAccessories - findByDisplacement - findByPower
    // - findByRegistrationYear - findByPrice - findByDiscount - findByIsNew - findByVehicleStatus - findByVehicleType
}
