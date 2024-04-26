package com.develhope.spring.Rent.Serivices;

import com.develhope.spring.EntityVehicle.Vehicle;
import com.develhope.spring.Rent.DTO.ModifyRentDTO;
import com.develhope.spring.Rent.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.Rent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentService {

    @Autowired
    private RentRepository rentRepository;

    //inserisci un nuovo noleggio
    public Rent createRent(RentDTO rentDTO) {
        Vehicle vehicle = vehicleRepository.findById(rentDTO.getVehicleId()).orElse(null);
        if (vehicle == null) {
            // veicolo non trovato, null
            return null;
        }

        Rent rent = new Rent();
        rent.setStartDate(rentDTO.getStartDate());
        rent.setEndDate(rentDTO.getEndDate());
        rent.setDailyCost(rentDTO.getDailyCost());
        rent.setIsPaid(rentDTO.getIsPaid());
        rent.setVehicle(vehicle);
        // calcola costo totale
        rent.setTotalCost(rent.calculateTotalCost());
        //salva e restituisce il noleggio
        return rentRepository.save(rent);
    }

    //ottieni i noleggi listati
    public List<Rent> rentList() {
        return rentRepository.findAll();
    }

    //ottieni da id
    public Rent getRentById(Long id) {
        return rentRepository.findById(id).orElse(null);
    }

    //aggiorna le date del noleggio esistente
    public Rent updateRentDates(Long id, LocalDate newStartDate, LocalDate newEndDate) {
        Rent rentToUpdate = rentRepository.findById(id).orElse(null);
        if (rentToUpdate == null) {
            // non trovato, null
            return null;
        }

        rentToUpdate.setStartDate(newStartDate);

        rentToUpdate.setEndDate(newEndDate);

        // ricalcolo costo noleggio
        rentToUpdate.setTotalCost(rentToUpdate.calculateTotalCost());

        //salva e restituisce il noleggio
        return rentRepository.save(rentToUpdate);
    }

    //elimina uun noleggio esistente
    public void deleteRent(Long id) {
        rentRepository.deleteById(id);
    }

}
