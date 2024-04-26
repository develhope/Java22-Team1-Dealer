package com.develhope.spring.Rent.Controllers;

import com.develhope.spring.Rent.DTO.ModifyRentDTO;
import com.develhope.spring.Rent.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.Rent;
import com.develhope.spring.Rent.Serivices.RentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentController {

    @Autowired
    private RentService rentService;

    // inserisci un nuovo noleggio
    @PostMapping("/create")
    public ResponseEntity<Rent> createRent(@RequestBody RentDTO rentDTO) {
        Rent rent = rentService.createRent(rentDTO);
        if (rent == null) {
            // se il noleggio e' null, errore
            return ResponseEntity.badRequest().build();
        }
        // noleggio creato messaggio created
        return new ResponseEntity<>(rent, HttpStatus.CREATED);
    }

    // ottieni la lista dei noleggi
    @GetMapping("/list")
    public ResponseEntity<List<Rent>> rentList() {
        List<Rent> rentals = rentService.rentList();
        // Restituisci la lista dei noleggi messaggio ok
        return new ResponseEntity<>(rentals, HttpStatus.OK);
    }

    // ottieni un noleggio da id
    @GetMapping("/search/{id}")
    public ResponseEntity<Rent> getRentById(@PathVariable Long id) {
        Rent rent = rentService.getRentById(id);
        if (rent == null) {
            // se il noleggio e' null, errore
            return ResponseEntity.notFound().build();
        }
        // restituisci il noleggio trovato con messaggio ok
        return new ResponseEntity<>(rent, HttpStatus.OK);
    }

    // modifica le date di un noleggio
    @PutMapping("/update/{id}")
    public ResponseEntity<Rent> updateRentDates(@PathVariable Long id, @RequestBody ModifyRentDTO modifyRentDTO) {
        Rent updatedRent = rentService.updateRentDates(id, modifyRentDTO.getStartDate(), modifyRentDTO.getEndDate());
        if (updatedRent == null) {
            // se il noleggio non viene trovato, errore
            return ResponseEntity.notFound().build();
        }
        // noleggio aggiornato messaggio ok
        return new ResponseEntity<>(updatedRent, HttpStatus.OK);
    }
    // Elimina un noleggio
    @DeleteMapping("/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRent(@PathVariable Long id) {
        // elimina il noleggio by id
        rentService.deleteRent(id);
    }


}


